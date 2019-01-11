package com.dua3.fx.util;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/** 
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts. 
 */
public class FileChooserBuilder {
	private File initialDir = new File(System.getProperty("user.home"));
	private String initialFileName = "";
	private List<FileChooser.ExtensionFilter> filter = new LinkedList<>();

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
		chooser.getExtensionFilters().setAll(filter);
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
	
	public FileChooserBuilder addFilter(String name, String... pattern) {
		ExtensionFilter f = new ExtensionFilter(name, pattern);
		filter.add(f);
		return this;
	}

	public FileChooserBuilder filter(List<ExtensionFilter> filters) {
		this.filter = new LinkedList<>(filters);
		return this;
	}

	public FileChooserBuilder filter(ExtensionFilter... filters) {
		this.filter = new LinkedList<>(Arrays.asList(filters));
		return this;
	}
}
