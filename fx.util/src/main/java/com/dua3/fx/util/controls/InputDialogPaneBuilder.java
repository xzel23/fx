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

package com.dua3.fx.util.controls;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.dua3.fx.util.controls.InputDialogPane.InputControl;
import com.dua3.fx.util.controls.InputDialogPane.Meta;
import com.dua3.utility.lang.LangUtil;

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
public class InputDialogPaneBuilder extends StandardDialogPaneBuilder<InputDialogPane, InputDialogPaneBuilder, Map<String, Object>> {

	public InputDialogPaneBuilder() {
		super(InputDialogPane::new);
	}

	private int columns = 1;

	private LinkedHashMap<String, InputDialogPane.Meta<?>> data = new LinkedHashMap<>();

	public <T> InputDialogPaneBuilder add(String id, String label, Class<T> type, T dflt, InputControl<T> control) {
		Objects.requireNonNull(id);
		Meta<T> meta = new Meta<T>(id, label, type, dflt, control);
		Meta<?> prev = data.put(id, meta);		
		LangUtil.check(prev == null, "Input with id '" + id + "' already defined");
		return this;
	}
	
	public InputDialogPaneBuilder columns(int columns) {
		LangUtil.check(columns > 0);
		this.columns = columns;
		return this;
	}

	public InputDialogPaneBuilder text(String id, String label, String dflt) {
		return text(id, label, dflt, s -> Optional.empty());
	}
	
	public InputDialogPaneBuilder text(String id, String label, String dflt, Function<String,Optional<String>> validate) {
		return add(id, label, String.class, dflt,
				new InputControl<String>() {
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

	public InputDialogPaneBuilder integer(String id, String label, Integer dflt) {
		return integer(id, label, dflt, i -> Optional.empty());
	}
	
	public InputDialogPaneBuilder integer(String id, String label, Integer dflt, Function<Integer,Optional<String>> validate) {
		return add(id, label, Integer.class, dflt,
				new InputControl<Integer>() {
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

	public InputDialogPaneBuilder decimal(String id, String label, Double dflt) {
		return decimal(id, label, dflt, d -> Optional.empty());
	}
	
	public InputDialogPaneBuilder decimal(String id, String label, Double dflt, Function<Double,Optional<String>> validate) {
		return add(id, label, Double.class, dflt,
				new InputControl<Double>() {
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

	public InputDialogPaneBuilder checkBox(String id, String label, boolean dflt, String text) {
		return add(id, label, Boolean.class, dflt,
				new InputControl<Boolean>() {
					final CheckBox control = new CheckBox(text);

					{
						control.setSelected(dflt);
					}
					
					@Override
					public Control control() {
						return control;
					}

					@Override
					public Boolean get() {
						return control.isSelected();
					}

					@Override
					public void set(Boolean arg) {
						control.setSelected(arg);;
					}

					@Override
					public Optional<String> validate() {
						return Optional.empty();
					}
				});
	}

	public <T> InputDialogPaneBuilder combobox(String id, String label, T dflt, Class<T> cls, Collection<T> items) {
		return add(id, label, cls, dflt,
				new InputControl<T>() {
					final ComboBox<T> control = new ComboBox<>();
 
					{
						control.setItems(FXCollections.observableArrayList(items));
						control.setValue(dflt);
					}
					
					@Override
					public Control control() {
						return control;
					}

					@Override
					public T get() {
						return control.getValue();
					}

					@Override
					public void set(T arg) {
						control.setValue(arg);
					}

					@Override
					public Optional<String> validate() {
						T t = control.getValue();
						if (t == null) {
							return Optional.of("No value selected.");
						}
						return Optional.empty();
					}
				});
	}

	public <T> InputDialogPaneBuilder radioList(String id, String label, T dflt, Class<T> cls, Collection<T> items) {
		return null; // FIXME
	}

	// TODO: add date and time inputs
	
	@Override
	public InputDialogPane build() {
		InputDialogPane pane = super.build();

		pane.setContent(data.values(), columns);

		return pane;
	}
}
