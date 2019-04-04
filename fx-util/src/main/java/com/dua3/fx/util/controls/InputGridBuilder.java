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

import com.dua3.fx.util.controls.InputGrid.Meta;
import com.dua3.utility.lang.LangUtil;
import com.dua3.utility.options.OptionSet;
import com.dua3.utility.options.OptionValues;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Builder for Alert Dialogs.
 *
 * Provides a fluent interface to create Alerts.
 */
public class InputGridBuilder
implements InputBuilder<InputGridBuilder> {

	public InputGridBuilder() {
		super();
	}

	private int columns = 1;

	private LinkedHashMap<String, InputGrid.Meta<?>> data = new LinkedHashMap<>();

    /* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#add(java.lang.String, java.lang.String, java.lang.Class, T, com.dua3.fx.util.controls.InputDialogPane.InputControl)
     */
    @Override
    public <T> InputGridBuilder add(String id, String label, Class<T> type, Supplier<T> dflt, InputControl<T> control) {
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
    public <T> InputGridBuilder add(String id, Class<T> type, Supplier<T> dflt, InputControl<T> control) {
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
    public InputGridBuilder columns(int columns) {
		LangUtil.check(columns > 0);
		this.columns = columns;
		return this;
	}

	/* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#text(java.lang.String, java.lang.String, java.lang.String, java.util.function.Function)
     */
	@Override
    public InputGridBuilder string(String id, String label, Supplier<String> dflt, Function<String,Optional<String>> validate) {
		return add(id, label, String.class, dflt, InputControl.stringInput(dflt, validate));
	}

	/* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#integer(java.lang.String, java.lang.String, java.lang.Integer, java.util.function.Function)
     */
	@Override
    public InputGridBuilder integer(String id, String label, Supplier<Integer> dflt, Function<Integer,Optional<String>> validate) {
		return add(id, label, Integer.class, dflt, InputControl.integerInput(dflt, validate));
	}

	/* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#decimal(java.lang.String, java.lang.String, java.lang.Double, java.util.function.Function)
     */
	@Override
    public InputGridBuilder decimal(String id, String label, Supplier<Double> dflt, Function<Double,Optional<String>> validate) {
		return add(id, label, Double.class, dflt, InputControl.decimalInput(dflt, validate));
	}

	/* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#checkBox(java.lang.String, java.lang.String, boolean, java.lang.String)
     */
	@Override
    public InputGridBuilder checkBox(String id, String label, Supplier<Boolean> dflt, String text) {
		return add(id, label, Boolean.class, dflt, InputControl.checkBoxInput(dflt, text));
	}

    /* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#comboBox(java.lang.String, java.lang.String, T, java.lang.Class, java.util.Collection)
     */
    @Override
    public <T> InputGridBuilder comboBox(String id, String label, Supplier<T> dflt, Class<T> cls, Collection<T> items) {
		return add(id, label, cls, dflt, InputControl.comboBoxInput(items, dflt));
	}

    /* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#radioList(java.lang.String, java.lang.String, T, java.lang.Class, java.util.Collection)
     */
    @Override
    public <T> InputGridBuilder radioList(String id, String label, Supplier<T> dflt, Class<T> cls, Collection<T> items) {
		 return add(id, label, cls, dflt, new RadioPane<>(items, null));
	}
	
	@Override
	public InputGridBuilder options(String id, String label, Supplier<OptionValues> dflt, Supplier<OptionSet> options) {
		return add(id, label, OptionValues.class, dflt, new OptionsPane(options, dflt));
	}

	public InputGridBuilder options(String id, Supplier<OptionValues> dflt, Supplier<OptionSet> options) {
		return add(id, OptionValues.class, dflt, new OptionsPane(options, dflt));
	}

	@Override
	public InputGridBuilder chooseFile(String id, String label, Supplier<File> dflt, FileDialogMode mode, FileChooser.ExtensionFilter filter) {
		return add(id, label, File.class, dflt, new FileInput(mode, dflt, filter));
	}

	/* (non-Javadoc)
     * @see com.dua3.fx.util.controls.InputBuilder#build()
     */
	public InputGrid build() {
		InputGrid grid = new InputGrid();

		grid.setContent(data.values(), columns);

		return grid;
	}
}