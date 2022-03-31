package com.dua3.fx.controls;

import com.dua3.fx.icons.IconView;
import com.dua3.fx.util.ValidationResult;
import com.dua3.utility.lang.LangUtil;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Validator {
    private static final Logger LOG = Logger.getLogger(Validator.class.getName());
    
    private final ResourceBundle resources;
    private final LinkedHashMap<Control, List<Supplier<ValidationResult>>> controls = new LinkedHashMap<>();
    private int iconSize = 20;
    
    /**
     * Creates a Validator instance without assigning a resource bundle.
     */
    public Validator() {
        this(null);
    }

    /**
     * Creates a Validator instance.
     * @param resources the resource bundle to look up message texts
     */
    public Validator(ResourceBundle resources) {
        this.resources = resources;
    }

    /**
     * Get list of rules for control. If the control is not registered yet, it will be assigned a newly generated
     * empty list.
     * @param c the control
     * @return the list of rules
     */
    private List<Supplier<ValidationResult>> rules(Control c) {
        return controls.computeIfAbsent(c, this::createRuleList);
    }

    /**
     * Creates a new rule list and sets the control to be validated on focus change.
     * @param control the control to create a rule list for
     * @return new rule list for the control
     */
    private List<Supplier<ValidationResult>> createRuleList(Control control) {
        control.setFocusTraversable(true);
        control.focusedProperty().addListener( (v,o,n) -> this.validate(control));
        return new ArrayList<>();
    }

    /**
     * Validate that a text has been entered.
     * @param c the control
     * @param message the message to display if validation fails
     */
    public void notEmpty(TextInputControl c, String message) {
        rules(c).add( () -> !c.getText().isEmpty() ? ValidationResult.ok() : ValidationResult.error(message) );
    }

    /**
     * Validate that entered text matches regular expression.
     * @param c the control
     * @param message the message to display if validation fails
     * @param regex the regular expression to test the control's text
     */
    public void matches(TextInputControl c, String message, String regex) {
        rules(c).add( () -> c.getText().matches(regex) ? ValidationResult.ok() : ValidationResult.error(message) );
    }

    /**
     * Custom validation.
     * @param c the control
     * @param message the message to display if validation fails
     * @param test the test to perform the validation
     */
    public void check(Control c, String message, BooleanSupplier test) {
        rules(c).add( () -> test.getAsBoolean() ? ValidationResult.ok() : ValidationResult.error(message) );
    }

    /**
     * Validate rules for a control. Rule violations are sorted by severity and the highest severity level is returned.
     * Decorations indicating the rule violation are automatically set, and if a message is present, a tooltip
     * is also created.
     * 
     * @param c the control
     * @return the validation result
     */
    public ValidationResult.Level validate(Control c) {
        var vr = rules(c).stream()
                .map(Supplier::get)
                .min((a, b) -> b.level().compareTo(a.level()))
                .orElseGet(ValidationResult::ok);

        // remove decorations
        Decoration.getDecorations(c).clear();
        
        String iconId = null;
        Paint paint = null;
        switch (vr.level()) {
            case OK:
                break;
            case ERROR:
                iconId = "fth-circle-cross";
                paint = Color.RED;
                break;
        }
        
        if (iconId != null) {
            IconView icon = new IconView();
            icon.setIconIdentifier(iconId);
            icon.setIconColor(paint);
            icon.setIconSize(iconSize);
            icon.setStyle(String.format("-fx-translate-x: -%d;", (int) (1.25*iconSize/2.0 + 0.5)));
            icon.setFocusTraversable(false);

            String message = getMessage(vr.message());
            if (!message.isEmpty()) {
                Tooltip.install(icon, new Tooltip(message));
            }

            Decoration.addDecoration(c, Pos.CENTER_RIGHT, icon, getClass().getName());
        } else {
            Decoration.removeDecoration(c, getClass().getName());
        }
        
        return vr.level();
    }

    private String getMessage(String m) {
        if (m == null || m.isEmpty()) {
            return "";
        }
        
        if (resources==null) {
            return m;
        }
        
        try {
            return resources.getString(m);
        } catch (MissingResourceException e) {
            LOG.log(Level.WARNING, "resource string not found: "+m, e);   
            return "";
        }
    }

    /**
     * Validate all rules of this validator.
     * @return highest level returned by any rule
     */
    public ValidationResult.Level validate() {
        return controls.keySet().stream()
                .map(this::validate)
                .max(ValidationResult.Level::compareTo)
                .orElse(ValidationResult.Level.OK);
    }

    /**
     * Set the icon size for notification icons
     * @param iconSize the icon size
     */
    public void setIconSize(int iconSize) {
        LangUtil.check(iconSize>=0 );
        this.iconSize = iconSize;
    }

    /**
     * Add validation for a button. If validation is added to a button, the validation will be performed when
     * the buttons is pressed, and if validation fails, the button clicked event will be consumed.
     * @param button the button to add validation to
     */
    public void addValidation(Button button) {
        if (button==null) {
            LOG.warning("addValidation(): button is null");
            return;
        }
        
        button.addEventFilter(ActionEvent.ACTION, ae -> {
            if (validate()!= ValidationResult.Level.OK) {
                ae.consume(); //not valid
            }
        });
    }
    
    public void focusFirst() {
        controls.keySet().stream().findFirst().ifPresent(Control::requestFocus);
    }
}
