package com.dua3.fx.util;

import java.util.Objects;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

/** 
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts. 
 */
@SuppressWarnings("exports") 
public class AlertBuilder 
extends AbstractDialogBuilder<ButtonType, Alert, AlertBuilder> {
	AlertBuilder(AlertType type) {
		super(() -> new Alert(type));
	}

	private ButtonType[] buttons;
	private ButtonType defaultButton;

	/**
	 * Create Alert instance.
	 * @return Alert instance
	 */
	public Alert build() {
		Alert dlg = super.build();
		
		if (buttons != null) {
			dlg.getButtonTypes().setAll(buttons);
		}

		if (defaultButton != null) {
			DialogPane pane = dlg.getDialogPane();
			for (ButtonType t : dlg.getButtonTypes()) {
				((Button) pane.lookupButton(t)).setDefaultButton(t == defaultButton);
			}
		}

		return dlg;
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
	
}
