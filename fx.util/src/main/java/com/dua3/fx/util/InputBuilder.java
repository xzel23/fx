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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.dua3.utility.lang.LangUtil;

import javafx.scene.control.Control;
import javafx.scene.control.TextField;

/**
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts.
 */
public class InputBuilder extends AbstractDialogBuilder<Map<String, Object>, InputDialog, InputBuilder> {

	public InputBuilder() {
		super(InputDialog::new);
		title("");
	}

	private int columns = 1;

	private LinkedHashMap<String, InputDialog.Meta<?>> data = new LinkedHashMap<>();

	public InputBuilder columns(int columns) {
		LangUtil.check(columns > 0);
		this.columns = columns;
		return this;
	}

	public InputBuilder text(String id, String label, String dflt) {
		data.put(id, new InputDialog.Meta<String>(id, label, String.class, dflt,
				() -> new InputDialog.InputControl<String>() {
					final TextField control = new TextField();

					@Override
					public Control control() {
						return control;
					}

					@Override
					public String get() {
						return control.getText();
					}

					@Override
					public void set(String arg) {
						control.setText(arg);
					}
				}));
		return this;
	}

	public InputBuilder integer(String id, String label, Integer dflt) {
		var prev = data.put(id, new InputDialog.Meta<Integer>(id, label, Integer.class, dflt,
				() -> new InputDialog.InputControl<Integer>() {
					final TextField control = new TextField();

					@Override
					public Control control() {
						return control;
					}

					@Override
					public Integer get() {
						return Integer.parseInt(control.getText());
					}

					@Override
					public void set(Integer arg) {
						control.setText(Integer.toString(arg));
					}
				}));
		LangUtil.check(prev == null, "Input with id '" + id + "' already defined");
		return this;
	}

	public InputBuilder decimal(String id, String label, Double dflt) {
		data.put(id, new InputDialog.Meta<Double>(id, label, Double.class, dflt,
				() -> new InputDialog.InputControl<Double>() {
					final TextField control = new TextField();

					@Override
					public Control control() {
						return control;
					}

					@Override
					public Double get() {
						return Double.parseDouble(control.getText());
					}

					@Override
					public void set(Double arg) {
						control.setText(Double.toString(arg));
					}
				}));
		return this;
	}

	/*
	 * public InputBuilder date(String id, String label, LocalDate dflt) { return
	 * input(id, label, () -> new TextField()); }
	 * 
	 * public InputBuilder time(String id, String label, LocalTime dflt) { return
	 * input(id, label, () -> new TextField()); }
	 * 
	 * public InputBuilder bool(String id, String label, Boolean dflt) { return
	 * input(id, label, () -> new TextField()); }
	 * 
	 * public InputBuilder list(String id, String label, Object dflt, Object...
	 * options) { return input(id, label, () -> new TextField()); }
	 */
	@Override
	public InputDialog build() {
		InputDialog dlg = super.build();

		dlg.setContent(data.values(), columns);

		return dlg;
	}
}