package com.dua3.fx.util.controls;

import com.dua3.utility.lang.LangUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
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

	protected Node createButton(ButtonType buttonType) {
		// a wizard dialog should only close when finish or cancel is clicked
		if (LangUtil.isOneOf(buttonType, ButtonType.OK, ButtonType.FINISH, ButtonType.CANCEL)) {
			return super.createButton(buttonType);
		}

		final Button button = new Button(buttonType.getText());
		final ButtonBar.ButtonData buttonData = buttonType.getButtonData();
		ButtonBar.setButtonData(button, buttonData);
		button.setDefaultButton(buttonData.isDefaultButton());
		button.setCancelButton(buttonData.isCancelButton());

		return button;
	}
}
