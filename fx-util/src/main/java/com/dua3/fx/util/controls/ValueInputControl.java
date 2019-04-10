package com.dua3.fx.util.controls;

import com.dua3.utility.options.Option;
import com.dua3.utility.options.Option.Value;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

import java.util.Objects;

/**
 * Interface for an input field.
 *
 * @param <T> the input result type
 */
public class ValueInputControl<T> implements InputControl<Value<T>> {
	private final InputControl<T> wrapped;
	private Property<Value<T>> value;


	public ValueInputControl(InputControl<T> ic) {
		this.wrapped = Objects.requireNonNull(ic);

		T current = wrapped.get();
		this.value = new SimpleObjectProperty<>(current == null ? null : Option.value(current));

		// bind value to wrapped value
		value.addListener((v,o,n) -> {
			wrapped.set(n.get());
		});
		wrapped.valueProperty().addListener((v,o,n) -> {
			value.setValue((Option.value(n)));
		});
	}

	/**
	 * Get the Node for this input element.
	 * 
	 * @return the node
	 */
	public Node node() {
		return wrapped.node();
	}

	/**
	 * Set/update control state.
	 */
	public void init() {
		wrapped.init();
	}
	
	/**
	 * Reset value to default
	 */
	public void reset() {
		wrapped.reset();
	}
	
	@Override
	public Property<Value<T>> valueProperty() {
		return value;
	}

	@Override
	public ReadOnlyBooleanProperty validProperty() {
		return wrapped.validProperty();
	}

	@Override
	public ReadOnlyStringProperty errorProperty() {
		return wrapped.errorProperty();
	}
	
}