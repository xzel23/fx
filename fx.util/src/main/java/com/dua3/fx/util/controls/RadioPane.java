package com.dua3.fx.util.controls;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

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

	/**
	 * Create new Radio Pane.
	 * @param items
	 *  the selectable items
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
		
		group.selectToggle(this.items.get(currentValue));
	}

	@SuppressWarnings("unchecked")
    @Override
    public T get() {
		Toggle selectedToggle = group.getSelectedToggle();
        return selectedToggle != null ? (T) selectedToggle.getUserData() : null;
	}
	
	@Override
    public void set(T item) {
		group.selectToggle(items.get(item));
	}
	
    @Override
    public Node node() {
        return this;
    }

}
