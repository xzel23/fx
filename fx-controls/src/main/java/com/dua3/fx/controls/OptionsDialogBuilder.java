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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import com.dua3.utility.options.Option;
import com.dua3.utility.options.Arguments;
import javafx.stage.Window;

/**
 * Builder for Alert Dialogs.
 *
 * Provides a fluent interface to create Alerts.
 */
public class OptionsDialogBuilder extends AbstractDialogBuilder<OptionsDialog, OptionsDialogBuilder, Arguments> {

	public OptionsDialogBuilder(Window parentWindow) {
		super(parentWindow);
		setDialogSupplier(OptionsDialog::new);
	}

	private Collection<Option<?>> options = new ArrayList<>();
	private Arguments currentValues = Arguments.empty();

	@Override
	public OptionsDialog build() {
		OptionsDialog dlg = super.build();

		dlg.setOptions(options, currentValues);

		return dlg;
	}

	/**
	 * Set options.
	 * @param options
	 *  the options to set
	 * @return
	 *  this builder instance
	 */
	public OptionsDialogBuilder options(Collection<Option<?>> options) {
		this.options = Objects.requireNonNull(options);
		return this;
	}

	/**
	 * Set current values.
	 * @param currentValues
	 *  the currentValues to set
	 * @return
	 *  this builder instance
	 */
	public OptionsDialogBuilder currentValues(Arguments currentValues) {
		this.currentValues = Objects.requireNonNull(currentValues);
		return this;
	}
}
