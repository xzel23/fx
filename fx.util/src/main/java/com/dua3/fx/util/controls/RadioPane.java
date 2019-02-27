package com.dua3.fx.util.controls;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.dua3.utility.options.Option;
import com.dua3.utility.options.Option.ChoiceOption;
import com.dua3.utility.options.Option.StringOption;
import com.dua3.utility.options.Option.Value;
import com.dua3.utility.options.OptionSet;
import com.dua3.utility.options.OptionValues;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

public class RadioPane<T> extends GridPane {

    /** Logger */
    protected static final Logger LOG = Logger.getLogger(RadioPane.class.getSimpleName());

	private final Collection<T> items;
	private final ToggleGroup group;
	private final T currentValue;
	private final T newValue;

	private static final Insets INSETS = new Insets(2);

	/**
	 * Create new OptionPane.
	 * @param optionSet
	 *  the available options
	 * @param currentValues
	 *  the current values
	 */
	public RadioPane(String labalText, Collection<T> items, T currentValue) {
		this.items = Objects.requireNonNull(items);
		this.currentValue = currentValue;
		this.newValue = currentValue;
		
		this.group = new ToggleGroup();
		// fixme selecteditemproperty/selecteditem
		int row = 0;
		for (var item: items) {
			Label label = row==0 ? new Label(labalText) : null;
			
			RadioButton control = new RadioButton(String.valueOf(item));
			control.setToggleGroup(group);

			addToGrid(label, 0, row);
			addToGrid(control, 1, row);
			
			row++;
		}
	}

	public T getSelectedItem() {
		return (T) group.getSelectedToggle();
	}
	
	private void addToGrid(Control child, int c, int r) {
		if (child != null) {
			add(child, c, r);
			setMargin(child, INSETS);
		}
	}

}
