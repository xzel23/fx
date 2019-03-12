package com.dua3.fx.util.controls;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;

class SimpleInputControl<C extends Control,R> implements  InputControl<R> {

	private final C control;
	private final State<R> state;
	private final Supplier<R> dflt;

	protected SimpleInputControl(C control, ObservableValue<R> value, Supplier<R> dflt, Function<R,Optional<String>> validate) {
		this.control = Objects.requireNonNull(control);
		this.state = new State<>(value);
		this.dflt = dflt;
		
		reset();
	}

	public void reset() {
		state.valueProperty().setValue(dflt.get());
	}
	
	@Override
	public C node() {
		return control;
	}

	@Override
	public Property<R> valueProperty() {
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