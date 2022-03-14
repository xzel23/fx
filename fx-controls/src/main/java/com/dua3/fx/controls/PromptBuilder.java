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

import javafx.scene.control.TextInputDialog;
import javafx.stage.Window;

/** 
 * Builder for Alert Dialogs.
 * <p> 
 * Provides a fluent interface to create Alerts. 
 */
public class PromptBuilder extends AbstractDialogBuilder<TextInputDialog, PromptBuilder, String> {
	public PromptBuilder(Window parentWindow) {
		super(parentWindow);
		setDialogSupplier(TextInputDialog::new);
		validate(r -> !r.isBlank()); // valid <=> not blank
	}
	
	public PromptBuilder defaultValue(String fmt, Object... args) {
		String defaultValue = args.length==0 ? fmt : String.format(fmt, args);
		setDialogSupplier(() -> new TextInputDialog(defaultValue));
		return this;
	}
	
	@Override
	public TextInputDialog build() {
		TextInputDialog dlg = super.build();
		dlg.setGraphic(null);
		return dlg;
	}
}
