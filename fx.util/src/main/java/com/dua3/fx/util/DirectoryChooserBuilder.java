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

package com.dua3.fx.util;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

/** 
 * Builder for directory chooser dialogs.
 * 
 * Provides a fluent interface to create file dialogs. 
 */
public class DirectoryChooserBuilder {
	private File initialDir = new File(System.getProperty("user.home"));

	DirectoryChooserBuilder() {
	}

	/**
	 * Show "Open" dialog.
	 * @param parent
	 *  the parent window
	 * @return
	 *  an Optional containing the selected file.
	 */
	public Optional<File> showDialog(Window parent) {
		DirectoryChooser chooser = build();
		return Optional.ofNullable(chooser.showDialog(parent));
	}

	private DirectoryChooser build() {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setInitialDirectory(initialDir);
		return chooser;
	}

	/**
	 * Set initial directory.
	 * @param initialDir
	 *  the initial directory
	 * @return
	 *  this instance
	 */
	public DirectoryChooserBuilder initialDir(File initialDir) {
		this.initialDir = Objects.requireNonNull(initialDir);
		return this;
	}

}