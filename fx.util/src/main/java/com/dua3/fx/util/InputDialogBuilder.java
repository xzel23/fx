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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.dua3.fx.util.InputDialog.Meta;
import com.dua3.utility.lang.LangUtil;

import javafx.scene.control.Control;
import javafx.scene.control.TextField;

/**
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts.
 */
public class InputDialogBuilder extends AbstractDialogBuilder<Map<String, Object>, InputDialog, InputDialogBuilder> {

	public InputDialogBuilder() {
		super(InputDialog::new);
		title("");
	}

	private int columns = 1;

	private LinkedHashMap<String, InputDialog.Meta<?>> data = new LinkedHashMap<>();

	public <T> InputDialogBuilder add(String id, String label, Class<T> type, T dflt, InputDialog.InputControl<T> control) {
		Objects.requireNonNull(id);
		Meta<T> meta = new InputDialog.Meta<T>(id, label, type, dflt, control);
		Meta<?> prev = data.put(id, meta);		
		LangUtil.check(prev == null, "Input with id '" + id + "' already defined");
		return this;
	}
	
	public InputDialogBuilder columns(int columns) {
		LangUtil.check(columns > 0);
		this.columns = columns;
		return this;
	}

	public InputDialogBuilder text(String id, String label, String dflt) {
		return text(id, label, dflt, s -> Optional.empty());
	}
	
	public InputDialogBuilder text(String id, String label, String dflt, Function<String,Optional<String>> validate) {
		return add(id, label, String.class, dflt,
				new InputDialog.InputControl<String>() {
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
					
					@Override
					public Optional<String> validate() {
						return validate.apply(get());
					}
				});
	}

	public InputDialogBuilder integer(String id, String label, Integer dflt) {
		return integer(id, label, dflt, i -> Optional.empty());
	}
	
	public InputDialogBuilder integer(String id, String label, Integer dflt, Function<Integer,Optional<String>> validate) {
		return add(id, label, Integer.class, dflt,
				new InputDialog.InputControl<Integer>() {
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
					
					@Override
					public Optional<String> validate() {
						String t = control.getText();
						try {
							int i = Integer.parseInt(t);
							return validate.apply(i);
						} catch (NumberFormatException e) {
							return Optional.of("'"+t+"' is no valid integer.");
						}
					}
				});
	}

	public InputDialogBuilder decimal(String id, String label, Double dflt) {
		return decimal(id, label, dflt, d -> Optional.empty());
	}
	
	public InputDialogBuilder decimal(String id, String label, Double dflt, Function<Double,Optional<String>> validate) {
		return add(id, label, Double.class, dflt,
				new InputDialog.InputControl<Double>() {
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

					@Override
					public Optional<String> validate() {
						String t = control.getText();
						try {
							double d = Double.parseDouble(t);
							return validate.apply(d);
						} catch (NumberFormatException e) {
							return Optional.of("'"+t+"' is no valid number.");
						}
					}
				});
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