package com.dua3.fx.util.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class InputDialogPane<R> extends DialogPane implements Supplier<R> {

	protected final BooleanProperty valid = new SimpleBooleanProperty(true);

	protected ArrayList<ButtonType> buttons = new ArrayList<>();

	public abstract void init();

	/**
	 * Get valid state property.
	 * @return the valid state property of the input
	 */
	public ReadOnlyBooleanProperty validProperty() {
		return valid;
	}

	private Predicate<R> validate = r -> true;

	protected void setValidate(Predicate<R> validate) {
		this.validate = Objects.requireNonNull(validate);
	}

	protected void updateValidState(R r) {
		valid.setValue(validate.test(r));
	}

	public void initButtons() {
		getButtonTypes().setAll(buttons);
	}
}
