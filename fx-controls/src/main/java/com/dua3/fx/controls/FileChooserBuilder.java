// Copyright 2019 Axel Howind
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.dua3.fx.controls;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

/** 
 * Builder for file open/save dialogs.
 * 
 * Provides a fluent interface to create file dialogs. 
 */
public class FileChooserBuilder {
	private static final Logger LOG = Logger.getLogger(FileChooserBuilder.class.getName());

	public static final File USER_HOME = new File(System.getProperty("user.home"));

	private File initialDir = USER_HOME;
	private String initialFileName = "";
	private List<ExtensionFilter> filters = new ArrayList<>();
	private ExtensionFilter selectedFilter = null;

	FileChooserBuilder() {
	}

	/**
	 * Show "Open" dialog.
	 * @param parent
	 *  the parent window
	 * @return
	 *  an Optional containing the selected file.
	 */
	public Optional<File> showOpenDialog(Window parent) {
		FileChooser chooser = build();
		return Optional.ofNullable(chooser.showOpenDialog(parent));
	}

	/**
	 * Show "Open multiple" dialog.
	 * @param parent
	 *  the parent window
	 * @return
	 *  a List containing the selected files, or an empty list if no files were selected
	 */
	public List<File> showOpenMultipleDialog(Window parent) {
		FileChooser chooser = build();
		List<File> files = chooser.showOpenMultipleDialog(parent);
		return files == null ? Collections.emptyList() : files;
	}

	/**
	 * Show "Save" dialog.
	 * @param parent
	 *  the parent window
	 * @return
	 *  an Optional containing the selected file.
	 */
	public Optional<File> showSaveDialog(Window parent) {
		FileChooser chooser = build();
		return Optional.ofNullable(chooser.showSaveDialog(parent));
	}

	private FileChooser build() {
		FileChooser chooser = new FileChooser();

		chooser.getExtensionFilters().setAll(filters);
		if (selectedFilter!=null) {
			chooser.setSelectedExtensionFilter(selectedFilter);
		}

		if (initialDir!=null && initialDir.isDirectory()) {
			try {
				chooser.setInitialDirectory(initialDir);
			} catch (IllegalArgumentException|SecurityException e) {
				LOG.warning("could not set initial directory: "+initialDir);
			}
		}
		
		chooser.setInitialFileName(initialFileName);
		
		return chooser;
	}

	/**
	 * Set initial filename and directory.
	 * @param file
	 *  the file
	 * @return
	 *  this instance
	 */
	public FileChooserBuilder initialFile(File file) {
		initialFileName(file.getName());
		initialDir(file.getParentFile());
		return this;
	}

	/**
	 * Set initial directory. If the initial directory is inaccessible or non-existent, it will be ignored when
	 * creating the dialog. If it exists, but is a regular file, the dialog will be created with the file's parent
	 * directory set as its initial directory.
	 * @param initialDir
	 *  the initial directory
	 * @return
	 *  this instance
	 */
	public FileChooserBuilder initialDir(File initialDir) {
		this.initialDir = initialDir != null ? initialDir : USER_HOME;
		return this;
	}

	/**
	 * Set initial filename.
	 * @param initialFileName
	 *  the initial filename
	 * @return
	 *  this instance
	 */
	public FileChooserBuilder initialFileName(String initialFileName) {
		this.initialFileName = initialFileName != null ? initialFileName : "";
		return this;
	}

	/** 
	 * Add filter to the list of filters.
	 * 
	 * @param name
	 *  the filter name
	 * @param pattern
	 *  the pattern(s) to use for this filter
	 * @return
	 *  this instance
	 */
	public FileChooserBuilder addFilter(String name, String... pattern) {
		ExtensionFilter f = new ExtensionFilter(name, pattern);
		filters.add(f);
		return this;
	}

	/**
	 * Set filters.
	 * <p>
	 * The current filters will be replaced.
	 * 
	 * @param filters
	 *  the filters to set
	 * @return
	 *  this instance
	 */
	public FileChooserBuilder filter(List<ExtensionFilter> filters) {
		this.filters = new ArrayList<>(filters);
		return this;
	}

	/**
	 * Set filters.
	 * <p>
	 * The current filters will be replaced.
	 * 
	 * @param filters
	 *  the filters to set
	 * @return
	 *  this instance
	 */
	public FileChooserBuilder filter(ExtensionFilter... filters) {
		this.filters = new ArrayList<>(Arrays.asList(filters));
		return this;
	}

	/**
	 * Set selected filter.
	 * <p>
	 * The filter is appended to the list of filters if not present.
	 * 
	 * @param f
	 *  the selected filter
	 * @return
	 *  this instance
	 */
	public FileChooserBuilder selectedFilter(ExtensionFilter f) {
		this.selectedFilter = f;
		if (f != null && !filters.contains(f)) {
			filters.add(f);
		}
		return this;
	}

}
