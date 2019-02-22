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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.dua3.fx.util.controls.OptionPane;
import com.dua3.utility.options.OptionSet;
import com.dua3.utility.options.OptionValues;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

/**
 * A Dialog for inputting values.
 *
 * The dialog consists of labels and input controls laid out in a grid.
 */
public class OptionsDialog extends Dialog<OptionValues> {

	private static final String MARKER_INITIAL = "";
	private static final String MARKER_ERROR = "\u26A0";
	private static final String MARKER_OK = "";
	
	public OptionsDialog() {		
		setResultConverter(btn -> {
			if (btn != ButtonType.OK) {
				return null;
			}

			// FIXME
			// Collecors.toMap() does not support null values!
//			Map<String,Object> result = new HashMap<>();
//			data.stream().forEach(e -> result.put(e.id, e.control.get()));
			//
//			return result;
			return null;
		});
	}

	/**
	 * Set options.
	 * @param optionSet 
	 *  the optionSet to set
	 * @param currentValues
	 *  the current values
	 */
	public void setOptions(OptionSet optionSet, OptionValues currentValues) {
		getDialogPane().setContent(new OptionPane(optionSet, currentValues));
	}

}
