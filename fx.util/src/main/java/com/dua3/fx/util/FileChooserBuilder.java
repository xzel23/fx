package com.dua3.fx.util;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import javafx.stage.FileChooser;
import javafx.stage.Window;

/** 
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts. 
 */
public class FileChooserBuilder {
	private File initialDir = new File(System.getProperty("user.home"));
	private String initialFileName = "";

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
		chooser.setInitialDirectory(initialDir);
		chooser.setInitialFileName(initialFileName);
		return chooser;
	}

	public FileChooserBuilder initialDir(File initialDir) {
		this.initialDir = Objects.requireNonNull(initialDir);
		return this;
	}

	public FileChooserBuilder initialFileName(String initialFileName) {
		this.initialFileName = Objects.requireNonNull(initialFileName);
		return this;
	}
}
