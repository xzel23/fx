package com.dua3.fx.util;

import javafx.scene.control.Alert.AlertType;

public class Dialogs {

	// utility - no instances
	private Dialogs() {}

	/**
	 * Start definition of new Alert dialog.
	 * @param type
	 * 	the type of the Alert
	 * @return 
	 * 	new {@link AlertBuilder} instance
	 */
	public static AlertBuilder alert(AlertType type) {
		return new AlertBuilder(type);
	}

	/**
	 * Start definition of new Confirmation dialog.
	 * 
	 * The confirmation text should be a closed question (i.e. the answered being either
	 * 'yes' or 'no').
	 * @param fmt
	 * 	the format String for building the confirmation text.
	 * @param args
	 *  the format arguments for building the confirmation text
	 * @return 
	 * 	new {@link ConfirmationBuilder} instance
	 */
	public static ConfirmationBuilder confirmation(String fmt, Object... args) {
		return new ConfirmationBuilder(fmt, args);
	}

	/**
	 * Start definition of new FileChooser.
	 * @return 
	 * 	new {@link FileChooserBuilder} instance
	 */
	public static FileChooserBuilder chooseFile() {
		return new FileChooserBuilder();
	}
	
	/**
	 * Start definition of new AboutDialog.
	 * @return 
	 * 	new {@link AboutDialogBuilder} instance
	 */
	public static AboutDialogBuilder about() {
		return new AboutDialogBuilder();
	}
}
