package com.dua3.fx.util.controls;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.dua3.utility.options.Option;
import com.dua3.utility.options.Option.ChoiceOption;
import com.dua3.utility.options.Option.StringOption;
import com.dua3.utility.options.Option.Value;
import com.dua3.utility.options.OptionSet;
import com.dua3.utility.options.OptionValues;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    private final InputControl.State<OptionValues> state;
    
	private Supplier<OptionSet> options;
	private Supplier<OptionValues> dflt;
	private ObservableValue<OptionValues> value = new SimpleObjectProperty<>();

	private LinkedHashMap<Option<?>, InputControl<?>> items = new LinkedHashMap<>();

	
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
        this.dflt=dflt;
        this.state = new State<>(value, dflt);
	}

    public void init() {
        getChildren().clear();
        
        OptionSet optionSet = options.get();
        
		OptionValues values = new OptionValues(dflt.get());
		
		int row = 0;
		for (Option<?> option: optionSet) {
			Label label = new Label(option.getName());
			
			InputControl<?> control = createControl(values, option);
			items.put(option, control);
			
			addToGrid(label, 0, row);
			addToGrid(control.node(), 1, row);
			
			row++;
		}
		
		// create binding
		/*
	    OptionValues values = new OptionValues();
	    for (var entry: items.entrySet()) {
	        Option<?> option = entry.getKey();
	        Property<?> property = entry.getValue();
			Value<?> value = (Value<?>) property.getValue();
	        values.put(option, value);
	    }
		return values;
	    */

    }

	@SuppressWarnings("unchecked")
	private <T> InputControl<T> createControl(OptionValues values, Option<T> option) {
		InputControl<T> control;
		if (option instanceof StringOption) {
			var inputControl = InputControl.stringInput(() -> (String) dflt.get().get(option).get(), r -> Optional.empty());
			
			inputControl.valueProperty().addListener( (v,o,n) -> {
				values.put(option, Option.value(n));
			});
			
			values.addChangeListener( (v,o,n) -> {
				inputControl.valueProperty().setValue(Objects.toString(n.get(), ""));
			});
			
			return (InputControl<T>) inputControl;
		} else if (option instanceof ChoiceOption<?>) {
			var choices = FXCollections.observableList(((ChoiceOption<T>)option).getChoices());
			var inputControl = InputControl.comboBoxInput(() -> ((Value<T>)dflt.get().get(option)).get());
			
			inputControl.valueProperty().addListener( (v,o,n) -> {
				values.put(option, Option.value(n));
			});
			
			values.addChangeListener( (v,o,n) -> {
				inputControl.valueProperty().setValue(((Value<T>) n).get());
			});

			return inputControl;
		}
		
		LOG.warning("unknown option type: "+option.getClass().getName());
		return null;
	}

	private void addToGrid(Node node, int c, int r) {
		if (node != null) {
			add(node, c, r);
			setMargin(node, INSETS);
		}
	}
	
	@Override
    public OptionValues get() {
	    OptionValues values = new OptionValues();
	    for (var entry: items.entrySet()) {
	        Option<?> option = entry.getKey();
	        Property<?> property = entry.getValue().valueProperty();
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
            Property property = item.getValue().valueProperty();
            Value value = arg.get(option);
            property.setValue(value.get());
        }
    }

    @Override
    public Node node() {
        return this;
    }

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Property<OptionValues> valueProperty() {
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

}
