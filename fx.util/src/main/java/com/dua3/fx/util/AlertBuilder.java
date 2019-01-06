package com.dua3.fx.util;

import java.util.Objects;
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
public class AlertBuilder {
	AlertBuilder(AlertType type) {
		this.type = Objects.requireNonNull(type);
	}

	private AlertType type;
	private String title = null;
	private String header = null;
	private String text = null;
	private ButtonType[] buttons;
	private ButtonType defaultButton;

	/**
	 * Create Alert instance.
	 * @return Alert instance
	 */
	public Alert build() {
		Alert alert = new Alert(type);

		if (title != null) {
			alert.setTitle(title);
		}

		if (header != null) {
			alert.setHeaderText(header);
		}

		if (text != null) {
			alert.setContentText(text);
		}

		if (buttons != null) {
			alert.getButtonTypes().setAll(buttons);
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
	 * Set Alert title.
	 * @param fmt
	 * 	the format String as defined by {@link java.util.Formatter}
	 * @param args
	 * 	the arguments passed to the formatter
	 * @return 
	 * 	{@code this}
	 */
	public AlertBuilder title(String fmt, Object... args) {
		this.title = String.format(fmt, args);
		return this;
	}

	/**
	 * Set Alert header text.
	 * @param fmt
	 * 	the format String as defined by {@link java.util.Formatter}
	 * @param args
	 * 	the arguments passed to the formatter
	 * @return 
	 * 	{@code this}
	 */
	public AlertBuilder header(String fmt, Object... args) {
		this.header = String.format(fmt, args);
		return this;
	}

	/**
	 * Set Alert text.
	 * @param fmt
	 * 	the format String as defined by {@link java.util.Formatter}
	 * @param args
	 * 	the arguments passed to the formatter
	 * @return 
	 * 	{@code this}
	 */
	public AlertBuilder text(String fmt, Object... args) {
		this.text = fmt != null ? String.format(fmt, args) : "";
		return this;
	}

	/**
	 * Define Alert Buttons.
	 * @param buttons
	 * 	the buttons to show
	 * @return 
	 * 	{@code this}
	 */
	public AlertBuilder buttons(ButtonType... buttons) {
		this.buttons = Objects.requireNonNull(buttons);
		return this;
	}

	/**
	 * Define the default Buttons.
	 * @param button
	 * 	the button to use as default
	 * @return 
	 * 	{@code this}
	 */
	public AlertBuilder defaultButton(ButtonType button) {
		this.defaultButton = Objects.requireNonNull(button);
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