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
import java.util.function.Supplier;

import com.dua3.fx.util.controls.InputPane.Meta;
import com.dua3.utility.lang.LangUtil;
import com.dua3.utility.options.OptionSet;
import com.dua3.utility.options.OptionValues;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts.
 */
public class InputPaneBuilder
extends AbstractPaneBuilder<InputPane, InputPaneBuilder, Map<String, Object>>
implements InputBuilder<InputPaneBuilder> {

	public InputPaneBuilder() {
		super();
		setDialogSupplier(InputPane::new);
	}

	private int columns = 1;

	private LinkedHashMap<String, InputPane.Meta<?>> data = new LinkedHashMap<>();

    /* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#add(java.lang.String, java.lang.String, java.lang.Class, T, com.dua3.fx.util.controls.InputDialogPane.InputControl)
     */
    @Override
    public <T> InputPaneBuilder add(String id, String label, Class<T> type, Supplier<T> dflt, InputControl<T> control) {
        Objects.requireNonNull(id);
        Meta<T> meta = new Meta<>(id, label, type, dflt, control);
        Meta<?> prev = data.put(id, meta);      
        LangUtil.check(prev == null, "Input with id '" + id + "' already defined");
        return this;
    }
    
    /* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#add(java.lang.String, java.lang.String, java.lang.Class, T, com.dua3.fx.util.controls.InputDialogPane.InputControl)
     */
    @Override
    public <T> InputPaneBuilder add(String id, Class<T> type, Supplier<T> dflt, InputControl<T> control) {
        Objects.requireNonNull(id);
        Meta<T> meta = new Meta<>(id, null, type, dflt, control);
        Meta<?> prev = data.put(id, meta);      
        LangUtil.check(prev == null, "Input with id '" + id + "' already defined");
        return this;
    }
    
	/* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#columns(int)
     */
	@Override
    public InputPaneBuilder columns(int columns) {
		LangUtil.check(columns > 0);
		this.columns = columns;
		return this;
	}

	/* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#text(java.lang.String, java.lang.String, java.lang.String, java.util.function.Function)
     */
	@Override
    public InputPaneBuilder string(String id, String label, Supplier<String> dflt, Function<String,Optional<String>> validate) {
		return add(id, label, String.class, dflt,
				new InputControl<>() {
					final TextField control = new TextField();

					@Override
					public Node node() {
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

	/* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#integer(java.lang.String, java.lang.String, java.lang.Integer, java.util.function.Function)
     */
	@Override
    public InputPaneBuilder integer(String id, String label, Supplier<Integer> dflt, Function<Integer,Optional<String>> validate) {
		return add(id, label, Integer.class, dflt,
				new InputControl<>() {
					final TextField control = new TextField();

					@Override
					public Node node() {
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
						} catch (@SuppressWarnings("unused") NumberFormatException e) {
							return Optional.of("'"+t+"' is no valid integer.");
						}
					}
				});
	}

	/* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#decimal(java.lang.String, java.lang.String, java.lang.Double, java.util.function.Function)
     */
	@Override
    public InputPaneBuilder decimal(String id, String label, Supplier<Double> dflt, Function<Double,Optional<String>> validate) {
		return add(id, label, Double.class, dflt,
				new InputControl<>() {
					final TextField control = new TextField();

					@Override
					public Node node() {
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
						} catch (@SuppressWarnings("unused") NumberFormatException e) {
							return Optional.of("'"+t+"' is no valid number.");
						}
					}
				});
	}

	/* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#checkBox(java.lang.String, java.lang.String, boolean, java.lang.String)
     */
	@Override
    public InputPaneBuilder checkBox(String id, String label, Supplier<Boolean> dflt, String text) {
		return add(id, label, Boolean.class, dflt,
				new InputControl<>() {
					final CheckBox control = new CheckBox(text);

					{
						control.setSelected(dflt.get());
					}
					
					@Override
					public Node node() {
						return control;
					}

					@Override
					public Boolean get() {
						return control.isSelected();
					}

					@Override
					public void set(Boolean arg) {
						control.setSelected(arg);
					}

					@Override
					public Optional<String> validate() {
						return Optional.empty();
					}
				});
	}

	/* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#comboBox(java.lang.String, java.lang.String, T, java.lang.Class, java.util.Collection)
     */
	@Override
    public <T> InputPaneBuilder comboBox(String id, String label, Supplier<T> dflt, Class<T> cls, Collection<T> items) {
		return add(id, label, cls, dflt,
				new InputControl<>() {
					final ComboBox<T> control = new ComboBox<>();
 
					{
						control.setItems(FXCollections.observableArrayList(items));
						control.setValue(dflt.get());
					}
					
					@Override
					public Node node() {
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

	/* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#radioList(java.lang.String, java.lang.String, T, java.lang.Class, java.util.Collection)
     */
	@Override
    public <T> InputPaneBuilder radioList(String id, String label, Supplier<T> dflt, Class<T> cls, Collection<T> items) {
		 return add(id, label, cls, dflt, new RadioPane<>(items, null));
	}
	
	@Override
	public InputPaneBuilder options(String id, String label, Supplier<OptionValues> dflt, Supplier<OptionSet> options) {
		return add(id, label, OptionValues.class, dflt, new OptionsPane(options, dflt));
	}

	public InputPaneBuilder options(String id, Supplier<OptionValues> dflt, Supplier<OptionSet> options) {
		return add(id, OptionValues.class, dflt, new OptionsPane(options, dflt));
	}

	// TODO: add date and time inputs
	
	/* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#build()
     */
	@Override
	public InputPane build() {
		InputPane pane = super.build();

		pane.setContent(data.values(), columns);

		return pane;
	}
}
