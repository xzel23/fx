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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.function.Supplier;

import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/** 
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts. 
 */
public class InputBuilder extends AbstractDialogBuilder<Map<String,Object>, Dialog<Map<String,Object>>, InputBuilder> {
	public InputBuilder() {
		super(Dialog::new);
		title("");
	}
	
	int columns = 0;
	
	private InputBuilder input(String id, String label, Supplier<Control> control) {
		return this;
	}

	public InputBuilder columns(int columns) {
		this.columns = columns;
		return this;
	}
	
	public InputBuilder text(String id, String label, String dflt) {
		return input(id, label, () -> new TextField());
	}
	
	public InputBuilder integer(String id, String label, Integer dflt) {
		return input(id, label, () -> new TextField());
	}
	
	public InputBuilder decimal(String id, String label, Double dflt) {
		return input(id, label, () -> new TextField());
	}
	
	public InputBuilder date(String id, String label, LocalDate dflt) {
		return input(id, label, () -> new TextField());
	}
	
	public InputBuilder time(String id, String label, LocalTime dflt) {
		return input(id, label, () -> new TextField());
	}
	
	public InputBuilder bool(String id, String label, Boolean dflt) {
		return input(id, label, () -> new TextField());
	}
	
	public InputBuilder list(String id, String label, Object dflt, Object... options) {
		return input(id, label, () -> new TextField());
	}
	
	@Override
	public Dialog<Map<String,Object>> build() {
		Dialog dlg = super.build();
		GridPane grid = new GridPane();
		dlg.getDialogPane().setContent(grid);
		return dlg;
	}
}