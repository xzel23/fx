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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.dua3.fx.util.InputDialog.Meta;
import com.dua3.utility.lang.LangUtil;
import com.dua3.utility.options.OptionValues;
import com.dua3.utility.options.OptionSet;

import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

/**
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts.
 */
public class OptionsDialogBuilder extends AbstractDialogBuilder<OptionValues, OptionsDialog, OptionsDialogBuilder> {

	public OptionsDialogBuilder() {
		super(OptionsDialog::new);
		title("");
	}

	private OptionSet options;
	private OptionValues currentValues;
	
	@Override
	public OptionsDialog build() {
		OptionsDialog dlg = super.build();

		dlg.setOptions(options);
		dlg.setCurrentValues(currentValues);

		return dlg;
	}

	/**
	 * @param options the options to set
	 */
	public OptionsDialogBuilder options(OptionSet options) {
		this.options = options;
		return this;
	}

	/**
	 * @param currentValues the currentValues to set
	 */
	public OptionsDialogBuilder currentValues(OptionValues currentValues) {
		this.currentValues = currentValues;
		return this;
	}
}
