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

import java.util.Objects;

import com.dua3.utility.options.OptionSet;
import com.dua3.utility.options.OptionValues;

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

	private OptionSet options = new OptionSet();
	private OptionValues currentValues = OptionValues.empty();
	
	@Override
	public OptionsDialog build() {
		OptionsDialog dlg = super.build();

		dlg.setOptions(options, currentValues);

		return dlg;
	}

	/**
	 * @param options the options to set
	 */
	public OptionsDialogBuilder options(OptionSet options) {
		this.options = Objects.requireNonNull(options);
		return this;
	}

	/**
	 * @param currentValues the currentValues to set
	 */
	public OptionsDialogBuilder currentValues(OptionValues currentValues) {
		this.currentValues = Objects.requireNonNull(currentValues);
		return this;
	}
}
