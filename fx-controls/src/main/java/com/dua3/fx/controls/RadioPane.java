package com.dua3.fx.controls;

import com.dua3.cabe.annotations.Nullable;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;


public class RadioPane<T> extends VBox implements InputControl<T> {

    /**
     * Logger
     */
    protected static final Logger LOG = LogManager.getLogger(RadioPane.class);
    private static final double SPACING = 4;
    private final LinkedHashMap<T, RadioButton> items = new LinkedHashMap<>();
    private final ToggleGroup group;
    private final InputControl.State<T> state;

    /**
     * Create new Radio Pane.
     *
     * @param items        the available items
     * @param currentValue the current value
     */
    @SuppressWarnings("unchecked")
    public RadioPane(Collection<T> items, @Nullable T currentValue) {
        this.group = new ToggleGroup();

        this.setSpacing(SPACING);
        ObservableList<Node> children = getChildren();
        for (var item : items) {
            RadioButton control = new RadioButton(String.valueOf(item));
            control.setUserData(item);
            control.setToggleGroup(group);
            children.add(control);
            this.items.put(item, control);
        }

        // update state when selected toggle changes
        Property<T> property = new SimpleObjectProperty<>();
        group.selectedToggleProperty().addListener((v, o, n) -> {
            Toggle toggle = group.getSelectedToggle();
            property.setValue(toggle != null ? (T) toggle.getUserData() : null);
        });

        this.state = new State<>(property);
        //noinspection VariableNotUsedInsideIf
        this.state.setValidate(v -> v == null ? Optional.of("Nothing selected.") : Optional.empty());

        // update toggle, when state changes
        state.valueProperty().addListener((v, o, n) -> group.selectToggle(this.items.get(n)));

        // set initial toggle
        group.selectToggle(this.items.get(currentValue));
    }

    @Override
    public Node node() {
        return this;
    }

    @Override
    public void reset() {
        state.reset();
    }

    @Override
    public Property<T> valueProperty() {
        return state.valueProperty();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return state.validProperty();
    }

    @Override
    public ReadOnlyStringProperty errorProperty() {
        return state.errorProperty();
    }

    @Override
    public void requestFocus() {
        if (group.getToggles().isEmpty()) {
            super.requestFocus();
        }

        Toggle t = group.getSelectedToggle();
        if (t == null) {
            t = group.getToggles().getFirst();

        }

        if (t instanceof Control) {
            ((Control) t).requestFocus();
        } else {
            super.requestFocus();
        }
    }
}
