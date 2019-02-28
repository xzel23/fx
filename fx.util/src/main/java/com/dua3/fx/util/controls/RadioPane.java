package com.dua3.fx.util.controls;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

public class RadioPane<T> extends GridPane {

    /** Logger */
    protected static final Logger LOG = Logger.getLogger(RadioPane.class.getSimpleName());

    private final LinkedHashMap<T, RadioButton> items = new LinkedHashMap<>();
	private final ToggleGroup group;
	private final T currentValue;

	private static final Insets INSETS = new Insets(2);

	/**
	 * Create new Radio Pane.
	 * @param labelText
	 *  the text to show on the label
	 * @param items
	 *  the selectable items
	 * @param currentValue
	 *  the current value
	 */
	public RadioPane(String labelText, Collection<T> items, T currentValue) {
		this.currentValue = currentValue;
		this.group = new ToggleGroup();
		
		int row = 0;
		for (var item: items) {
			Label label = row==0 ? new Label(labelText) : null;
			
			RadioButton control = new RadioButton(String.valueOf(item));
			control.setToggleGroup(group);

			addToGrid(label, 0, row);
			addToGrid(control, 1, row);
			
			this.items.put(item, control);
			
			row++;
		}
		
		group.selectToggle(this.items.get(currentValue));
	}

	public T getSelectedItem() {
		return (T) group.getSelectedToggle();
	}
	
	public void setSelectedItem(T item) {
		group.selectToggle(items.get(item));
	}
	
	private void addToGrid(Control child, int c, int r) {
		if (child != null) {
			add(child, c, r);
			setMargin(child, INSETS);
		}
	}

}
