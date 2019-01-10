package com.dua3.fx.util;

import javafx.scene.control.Alert.AlertType;

public class Dialogs {

	// utility - no instances
	private Dialogs() {}

	/**
	 * Start definition of new Alert dialog.
	 * @return 
	 * 	new {@link AlertBuilder} instance
	 */
	public static AlertBuilder warning() {
		return new AlertBuilder(AlertType.WARNING);
	}

	/**
	 * Start definition of new Alert dialog.
	 * @return 
	 * 	new {@link AlertBuilder} instance
	 */
	public static AlertBuilder error() {
		return new AlertBuilder(AlertType.ERROR);
	}

	/**
	 * Start definition of new Alert dialog.
	 * @return 
	 * 	new {@link AlertBuilder} instance
	 */
	public static AlertBuilder information() {
		return new AlertBuilder(AlertType.INFORMATION);
	}

	/**
	 * Start definition of new Alert dialog.
	 * @return 
	 * 	new {@link AlertBuilder} instance
	 */
	public static AlertBuilder confirmation() {
		return new AlertBuilder(AlertType.CONFIRMATION);
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

	public static PromptBuilder prompt() {
		return new PromptBuilder();
	}
}
