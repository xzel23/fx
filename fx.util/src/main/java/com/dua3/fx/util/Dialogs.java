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

import java.util.List;

import com.dua3.utility.options.Option;
import com.dua3.utility.options.OptionSet;
import com.dua3.utility.options.Options;

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
	 * Start definition of new DirectoryChooser.
	 * @return 
	 * 	new {@link DirectoryChooserBuilder} instance
	 */
	public static DirectoryChooserBuilder chooseDirectory() {
		return new DirectoryChooserBuilder();
	}
	
	/**
	 * Start definition of new AboutDialog.
	 * @return 
	 * 	new {@link AboutDialogBuilder} instance
	 */
	public static AboutDialogBuilder about() {
		return new AboutDialogBuilder();
	}

	/**
	 * Start definition of new prompt dialog.
	 * @return 
	 * 	new {@link PromptBuilder} instance
	 */
	public static PromptBuilder prompt() {
		return new PromptBuilder();
	}

	/**
	 * Start definition of new input dialog.
	 * @return 
	 * 	new {@link InputDialogBuilder} instance
	 */
	public static InputDialogBuilder input() {
		return new InputDialogBuilder();
	}

	public static OptionsDialogBuilder options() {
		return new OptionsDialogBuilder();
	}
}
