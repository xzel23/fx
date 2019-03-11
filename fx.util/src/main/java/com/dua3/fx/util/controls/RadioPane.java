package com.dua3.fx.util.controls;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class RadioPane<T> extends VBox implements InputControl<T> {

    /** Logger */
    protected static final Logger LOG = Logger.getLogger(RadioPane.class.getSimpleName());

    private final LinkedHashMap<T, RadioButton> items = new LinkedHashMap<>();
	private final ToggleGroup group;

	private static final double SPACING = 4;

	private final InputControl.State<T> state;

	/**
	 * Create new Radio Pane.
	 * @param items
	 *  the available items
	 * @param currentValue
	 *  the current value
	 */
	public RadioPane(Collection<T> items, T currentValue) {
		this.group = new ToggleGroup();

		this.setSpacing(SPACING);
		ObservableList<Node> children = getChildren();
		for (var item: items) {
			RadioButton control = new RadioButton(String.valueOf(item));
			control.setUserData(item);
			control.setToggleGroup(group);
			children.add(control);
			this.items.put(item, control);
		}

		// update state when selected toggle changes
		@SuppressWarnings("unchecked")
		ObservableValue<T> valueBinding = Bindings.createObjectBinding( () -> {
			Toggle selectedToggle = group.getSelectedToggle();
			return selectedToggle != null ? (T) selectedToggle.getUserData() : null;
		}, group.selectedToggleProperty());
		this.state = new State<>(valueBinding);
		
		// update toggle, when state changes
		state.valueProperty().addListener( (v,o,n) -> {
			group.selectToggle(this.items.get(n));
		});
		
		// set initial toggle
		group.selectToggle(this.items.get(currentValue));
	}

    @Override
    public Node node() {
        return this;
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
	public void reset() {
		state.reset();
	}
}
