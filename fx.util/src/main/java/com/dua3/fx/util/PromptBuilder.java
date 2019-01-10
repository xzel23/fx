package com.dua3.fx.util;

import javafx.scene.control.TextInputDialog;

/** 
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts. 
 */
@SuppressWarnings("exports")
public class PromptBuilder extends AbstractDialogBuilder<String, TextInputDialog, PromptBuilder> {
	public PromptBuilder() {
		super(TextInputDialog::new);
		title("");
	}
	
	public PromptBuilder defaultValue(String fmt, Object... args) {
		String defaultValue = String.format(fmt, args);
		setSupplier(() -> new TextInputDialog(defaultValue));
		return this;
	}
	
	@Override
	public TextInputDialog build() {
		TextInputDialog dlg = super.build();
		dlg.setGraphic(null);
		return dlg;
	}
}