package com.dua3.fx.util.controls;

import java.util.LinkedHashMap;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.dua3.utility.options.Option;
import com.dua3.utility.options.Option.ChoiceOption;
import com.dua3.utility.options.Option.StringOption;
import com.dua3.utility.options.Option.Value;
import com.dua3.utility.options.OptionSet;
import com.dua3.utility.options.OptionValues;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class OptionsPane extends GridPane implements InputControl<OptionValues>{

    /** Logger */
    protected static final Logger LOG = Logger.getLogger(OptionsPane.class.getSimpleName());

	private Supplier<OptionSet> options;
	private Supplier<OptionValues> currentValues;

	private LinkedHashMap<Option<?>, Property<?>> items = new LinkedHashMap<>();
	
	private static final Insets INSETS = new Insets(2);

	/**
	 * Create new OptionsPane.
	 * @param optionSet
	 *  the available options
	 * @param currentValues
	 *  the current values
	 */
    public OptionsPane(OptionSet optionSet, OptionValues currentValues) {
        this(() -> optionSet, () -> currentValues);
    }
    
    public OptionsPane(Supplier<OptionSet> options, Supplier<OptionValues> dflt) {
        this.options = options;
        this.currentValues=dflt;
	}

    public void init() {
        getChildren().clear();
        
        OptionSet optionSet = options.get();
		OptionValues values = currentValues.get();
		
		int row = 0;
		for (Option<?> option: optionSet) {
			Label label = new Label(option.getName());
			
            Property<?> property;            
			Control control;
			Value<?> value = values.get(option);
			if (option instanceof StringOption) {
				TextField c = new TextField();
				c.setText(value.text());
				control = c;
				StringProperty textProperty = c.textProperty();
				Property<Value<String>> valueProperty = new SimpleObjectProperty<Value<String>>();
				textProperty.addListener((v,o,n) -> {
					valueProperty.setValue(Option.value(n));
				});
				valueProperty.addListener((v,o,n) -> textProperty.set(n.get()));
				property = valueProperty;
			} else if (option instanceof ChoiceOption<?>) {
				var choices = FXCollections.observableList(((ChoiceOption<?>)option).getChoices());
				var c = new ComboBox<>(choices);
				c.getSelectionModel().select(choices.indexOf(value));
				control = c;
				property = c.valueProperty();
			} else {
				LOG.warning("unknown option type: "+option.getClass().getName());
				control = null;
				property = null;
			}
			
			items.put(option, property);
			
			addToGrid(label, 0, row);
			addToGrid(control, 1, row);
			
			row++;
		}
    }

	private void addToGrid(Control child, int c, int r) {
		if (child != null) {
			add(child, c, r);
			setMargin(child, INSETS);
		}
	}
	
	@Override
    public OptionValues get() {
	    OptionValues values = new OptionValues();
	    for (var entry: items.entrySet()) {
	        Option<?> option = entry.getKey();
	        Property<?> property = entry.getValue();
			Value<?> value = (Value<?>) property.getValue();
	        values.put(option, value);
	    }
		return values;
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void set(OptionValues arg) {
        for (var item: items.entrySet()) {
            Option option = item.getKey();
            Property property = item.getValue();
            Value value = arg.get(option);
            property.setValue(value.get());
        }
    }

    @Override
    public Node node() {
        return this;
    }

}
