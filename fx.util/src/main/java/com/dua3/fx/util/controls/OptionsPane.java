package com.dua3.fx.util.controls;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.dua3.fx.util.controls.InputDialogPane.InputControl;
import com.dua3.utility.options.Option;
import com.dua3.utility.options.Option.ChoiceOption;
import com.dua3.utility.options.Option.StringOption;
import com.dua3.utility.options.Option.Value;
import com.dua3.utility.options.OptionSet;
import com.dua3.utility.options.OptionValues;

import javafx.beans.property.Property;
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

	private final OptionSet options;
	private final OptionValues currentValues;
	private final OptionValues newValues;

	private LinkedHashMap<Option<?>, Property<?>> items;
	
	private static final Insets INSETS = new Insets(2);

	/**
	 * Create new OptionsPane.
	 * @param optionSet
	 *  the available options
	 * @param currentValues
	 *  the current values
	 */
	public OptionsPane(OptionSet optionSet, OptionValues currentValues) {
		this.options = Objects.requireNonNull(optionSet);
		this.currentValues = new OptionValues(currentValues);
		this.newValues = new OptionValues(currentValues);
		
		int row = 0;
		for (Option<?> option: optionSet) {
			Label label = new Label(option.getName());
			
            Property<?> property;            
			Control control;
			Value<?> value = currentValues.get(option);
			if (option instanceof StringOption) {
				TextField c = new TextField();
				c.setText(String.valueOf(value));
				newValues.put(option, () -> c.textProperty().get());
				control = c;
				property = c.textProperty();
			} else if (option instanceof ChoiceOption<?>) {
				var items = FXCollections.observableList(((ChoiceOption<?>)option).getChoices());
				var c = new ComboBox<>(items);
				c.getSelectionModel().select(items.indexOf(value));
				newValues.put(option, c.valueProperty().get());
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

	/**
	 * Get the options for this OptionsPane.
	 * @return
	 *  the options
	 */
	public OptionSet getOptions() {
		return options;
	}
	
	private void addToGrid(Control child, int c, int r) {
		if (child != null) {
			add(child, c, r);
			setMargin(child, INSETS);
		}
	}
	
	@Override
    public OptionValues get() {
		return new OptionValues(newValues.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().makeStatic())));
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
