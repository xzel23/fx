package com.dua3.fx.controls;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.control.Control;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

class SimpleInputControl<C extends Control, R> implements InputControl<R> {

    private final C control;
    private final State<R> state;
    private final Supplier<R> dflt;

    protected SimpleInputControl(C control, Property<R> value, Supplier<R> dflt, Function<R, Optional<String>> validate) {
        this.control = Objects.requireNonNull(control);
        this.state = new State<>(value, dflt, validate);
        this.dflt = dflt;

        reset();
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
    public void reset() {
        state.valueProperty().setValue(dflt.get());
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
