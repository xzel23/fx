package com.dua3.fx.controls;

import com.dua3.utility.options.Arguments;
import com.dua3.utility.options.ChoiceOption;
import com.dua3.utility.options.Flag;
import com.dua3.utility.options.Option;
import com.dua3.utility.options.SimpleOption;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import java.util.stream.Stream;

public class OptionsPane extends GridPane implements InputControl<Arguments>{

    /** Logger */
    protected static final Logger LOG = LoggerFactory.getLogger(OptionsPane.class);

    private final InputControl.State<Arguments> state;
    
	private final Supplier<Collection<Option<?>>> options;
	private final Supplier<Arguments> dflt;
	private final Property<Arguments> value = new SimpleObjectProperty<>();

	private final Map<Option<?>, InputControl<?>> items = new LinkedHashMap<>();

	
	private static final Insets INSETS = new Insets(2);

	/**
	 * Create new OptionsPane.
	 * @param optionSet
	 *  the available options
	 * @param currentValues
	 *  the current values
	 */
    public OptionsPane(Collection<Option<?>> optionSet, Arguments currentValues) {
        this(() -> optionSet, () -> currentValues);
    }
    
    public OptionsPane(Supplier<Collection<Option<?>>> options, Supplier<Arguments> dflt) {
        this.options = options;
        this.dflt=dflt;
        this.state = new State<>(value, dflt);
	}

	@Override
    public void init() {
        getChildren().clear();

		Collection<Option<?>> optionSet = options.get();
		Arguments values = dflt.get();
		
		int row = 0;
		for (Option<?> option: optionSet) {
			Label label = new Label(option.displayName());
			
			var control = createControl(values, option);
			items.put(option, control);
			
			addToGrid(label, 0, row);
			addToGrid(control.node(), 1, row);
			
			row++;
		}
		
		// create binding
		/* FIXME
	    Arguments values = new Arguments();
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
	private <T> InputControl<T> createControl(Arguments values, Option<T> option) {
		if (option instanceof ChoiceOption<?>) {
			ChoiceOption<T> co = (ChoiceOption<T>) option;
			return new ChoiceInputControl<>(co, () -> dflt.get().getOrThrow(co));
		} else if (option instanceof Flag f) {
			Supplier<Boolean> dfltValue = () -> (dflt.get().isSet(f));
			CheckBox checkBox = new CheckBox(f.displayName());
			return (InputControl<T>) new SimpleInputControl<>(checkBox, checkBox.selectedProperty(), dfltValue, x -> Optional.empty());
		} else if (option instanceof SimpleOption<T> so) {
			Supplier<T> df = () -> dflt.get().get(so).or(so::getDefault).orElse(null);
			StringConverter<T> converter = new StringConverter<>() {
				@Override
				public String toString(T v) {
					return option.format(v);
				}

				@Override
				public T fromString(String s) {
					return option.map(s);
				}
			};
			Function<T, Optional<String>> validator = s -> Optional.empty();
			return InputControl.stringInput(df, validator, converter);
		}

		throw new UnsupportedOperationException("unsupported input type: "+option.getClass().getName());
	}

	private void addToGrid(Node node, int c, int r) {
		if (node != null) {
			add(node, c, r);
			setMargin(node, INSETS);
		}
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
    public Arguments get() {
		Deque<Arguments.Entry<?>> entries = new ArrayDeque<>();
	    for (var entry: items.entrySet()) {
	        Option option = entry.getKey();
	        Object value = entry.getValue().valueProperty().getValue();
	        entries.add(Arguments.createEntry(option, value));
	    }
		return Arguments.of(entries.toArray(Arguments.Entry[]::new));
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void set(Arguments arg) {
        for (var item: items.entrySet()) {
            Option option = item.getKey();
			InputControl control = item.getValue();
			Stream<List<?>> stream = arg.stream(option);
			Optional<?> value = stream.filter(list -> !list.isEmpty())
					.reduce((first, second) -> second)
					.map(list -> list.get(list.size()-1));
			control.set(value.orElse(null));
        }
    }

    @Override
    public Node node() {
        return this;
    }

	@Override
	public void reset() {
    	items.forEach( (item,control) -> control.reset() );
	}

	@Override
	public Property<Arguments> valueProperty() {
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
