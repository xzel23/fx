package com.dua3.fx.util.controls;

import java.util.Objects;

import com.dua3.utility.options.Option;
import com.dua3.utility.options.OptionSet;
import com.dua3.utility.options.Options;
import com.dua3.utility.options.Option.ChoiceOption;
import com.dua3.utility.options.Option.StringOption;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class OptionPane extends GridPane {

	private final OptionSet optionSet;
	private final Options currentValues;
	private final Options newValues;

	private static final Insets INSETS = new Insets(2);

	/**
	 * Create new OptionPane.
	 * @param optionSet
	 *  the available options.
	 */
	public OptionPane(OptionSet optionSet, Options currentValues) {
		this.optionSet = Objects.requireNonNull(optionSet);
		this.currentValues = new Options(currentValues);
		this.newValues = new Options(currentValues);
		
		int row = 0;
		for (Option<?> option: optionSet) {
			Label label = new Label(option.getName());
			
			Control control;
			if (option instanceof StringOption) {
				TextField c = new TextField();
				newValues.put(option, () -> c.textProperty().get());
				control = c;
			} else if (option instanceof ChoiceOption<?>) {
				var items = FXCollections.observableList(((ChoiceOption<?>)option).getChoices());
				ComboBox<?> c = new ComboBox<>(items);
				newValues.put(option, () -> c.valueProperty());
				control = c;
			}
			
			addToGrid(label, 0, row);
			addToGrid(label, 1, row);
			
			row++;
		}
	}

	/**
	 * Get the options for this OptionPane.
	 * @return
	 *  the options
	 */
	public OptionSet getOptions() {
		return optionSet;
	}
	
	private void addToGrid(Control child, int c, int r) {
		add(child, c, r);
		setMargin(child, INSETS);
	}

}
