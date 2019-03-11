package com.dua3.fx.util.controls;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * Interface for an input field.
 *
 * @param <R> the input result type
 */
public interface InputControl<R> {
	/**
	 * Get the Node for this input element.
	 * 
	 * @return the node
	 */
	Node node();

	/**
	 * Get value.
	 * 
	 * @return the current value
	 */
	default R get() {
		return valueProperty().getValue();
	}

	/**
	 * Set value.
	 * 
	 * @param arg the value to set
	 */
	default void set(R arg) {
		valueProperty().setValue(arg);
	}
	
	/**
	 * Set/update control state.
	 */
	default void init() {
		// nop
	}
	
	/**
	 * Reset value to default
	 */
	void reset();
	
	Property<R> valueProperty();
	ReadOnlyBooleanProperty validProperty();
	ReadOnlyStringProperty errorProperty();
	
	class State<R> {
		private final Property<R> value = new SimpleObjectProperty<>();
		private final BooleanProperty valid = new SimpleBooleanProperty(true);
		private final StringProperty error = new SimpleStringProperty("");
		
		private Supplier<R> dflt;
		
		private Function<R,Optional<String>> validate = s -> Optional.empty();

		private static <R> Supplier<R> freeze(ObservableValue<R> value) {
			final R frozen = value.getValue();
			return () -> frozen;
		}
		
		public State(ObservableValue<R> value) {
			this(value, freeze(value));
			
		}
			
		public State(ObservableValue<R> value, Supplier<R> dflt) {
			this.value.bind(value);
			this.value.addListener( (v,o,n) -> updateValidState(n) );
			this.dflt = Objects.requireNonNull(dflt);
			updateValidState(this.value.getValue());
		}

		public void setValidate(Function<R,Optional<String>> validate) {
			this.validate = Objects.requireNonNull(validate);
		}

		private void updateValidState(R r) {
			Optional<String> result = validate.apply(r);
			valid.setValue(result.isEmpty());
			error.setValue(result.orElse(""));
		}

		public ReadOnlyBooleanProperty validProperty() {
			return valid;
		}

		public ReadOnlyStringProperty errorProperty() {
			return error;
		}

		public Property<R> valueProperty() {
			return value;
		}
		
		public void setDefault(Supplier<R> dflt) {
			this.dflt = Objects.requireNonNull(dflt);
		}
		
		public void reset() {
			value.setValue(dflt.get());
		}
	}
}