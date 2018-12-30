package com.dua3.fx.util;

import java.io.File;
import java.util.Optional;

import javafx.stage.FileChooser;
import javafx.stage.Window;

/** 
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts. 
 */
public class FileChooserBuilder {
	FileChooserBuilder() {
	}
	
	public Optional<File> showOpenDialog(Window parent) {
		FileChooser chooser = build();
		return Optional.ofNullable(chooser.showOpenDialog(parent));
	}

	public Optional<File> showSaveDialog(Window parent) {
		FileChooser chooser = build();
		return Optional.ofNullable(chooser.showSaveDialog(parent));
	}

	private FileChooser build() {
		FileChooser chooser = new FileChooser();
		return chooser;
	}
}
