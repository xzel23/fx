package com.dua3.fx.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;

/** 
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts. 
 */
public class ConfirmationBuilder {
	ConfirmationBuilder(String fmt, Object... args) {
		this.question = String.format(fmt, args);
	}
	
	private String question = null;
	private String title = null;
	private String detail = null;
	private boolean cancelable = false;
	private ButtonType defaultButton = ButtonType.CANCEL;

	/**
	 * Create Alert instance.
	 * @return Alert instance
	 */
	public Alert build() {
		Alert alert = new Alert(AlertType.CONFIRMATION);

		if (title != null) {
			alert.setTitle(title);
		}

		alert.setHeaderText(question);

		if (detail != null) {
			alert.setContentText(detail);
		}

		if (cancelable) {
			alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		} else {
			alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);			
		}

		if (defaultButton != null) {
			DialogPane pane = alert.getDialogPane();
			for (ButtonType t : alert.getButtonTypes()) {
				((Button) pane.lookupButton(t)).setDefaultButton(t == defaultButton);
			}
		}
		
		return alert;
	}

	/**
	 * Set Dialog title.
	 * @param fmt
	 * 	the format String as defined by {@link java.util.Formatter}
	 * @param args
	 * 	the arguments passed to the formatter
	 * @return 
	 * 	{@code this}
	 */
	public ConfirmationBuilder title(String fmt, Object... args) {
		this.title = String.format(fmt, args);
		return this;
	}

	/**
	 * Set Dialog text.
	 * @param fmt
	 * 	the format String as defined by {@link java.util.Formatter}
	 * @param args
	 * 	the arguments passed to the formatter
	 * @return 
	 * 	{@code this}
	 */
	public ConfirmationBuilder text(String fmt, Object... args) {
		this.detail = fmt != null ? String.format(fmt, args) : "";
		return this;
	}

	/**
	 * Make Dialog cancelable.
	 * @param flag
	 * 	flag to enable or disable cancellation of dialog
	 * @return 
	 * 	{@code this}
	 */
	public ConfirmationBuilder cancelable(boolean flag) {
		this.cancelable = flag;
		return this;
	}

	/**
	 * Define the default Buttons.
	 * @param button
	 * 	the button to use as default
	 * @return 
	 * 	{@code this}
	 */
	public ConfirmationBuilder defaultButton(ButtonType button) {
		this.defaultButton = button;
		return this;
	}

	/**
	 * Build and show the alert.
	 * 
	 * This is equivalent to calling build().showAndWait().
	 * 
	 * @return
	 *  Optinal containingg the button pressed as returned by Alert.showAndWait()
	 */
	public Optional<ButtonType> showAndWait() {
		return build().showAndWait();
	}
	
}