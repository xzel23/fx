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

import javafx.scene.control.TextInputDialog;

/** 
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts. 
 */
public class PromptBuilder extends StandardDialogBuilder<TextInputDialog, PromptBuilder, String> {
	public PromptBuilder() {
		super(TextInputDialog::new);
	}
	
	public PromptBuilder defaultValue(String fmt, Object... args) {
		String defaultValue = args.length==0 ? fmt : String.format(fmt, args);
		setSupplier(() -> new TextInputDialog(defaultValue));
		return this;
	}
	
	@Override
	public TextInputDialog build() {
		TextInputDialog dlg = super.build();
		dlg.setGraphic(null);
		return dlg;
	}
}