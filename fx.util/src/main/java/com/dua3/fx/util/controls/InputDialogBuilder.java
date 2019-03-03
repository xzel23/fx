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
import java.util.Map;
import java.util.Optional;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

import com.dua3.fx.util.controls.InputDialogPane.InputControl;
import com.dua3.utility.options.OptionSet;
import com.dua3.utility.options.OptionValues;

/**
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts.
 */
public class InputDialogBuilder
extends StandardDialogBuilder<InputDialog, InputDialogBuilder, Map<String, Object>> 
implements InputBuilder<InputDialogBuilder> {

	private final InputDialogPaneBuilder pb = new InputDialogPaneBuilder();
	
	public InputDialogBuilder() {
		super(InputDialog::new);
	}

	@Override
    public <T> InputDialogBuilder add(String id, String label, Class<T> type, T dflt, InputControl<T> control) {
		pb.add(id, label, type, dflt, control);
		return this;
	}
	
	@Override
    public InputDialogBuilder columns(int columns) {
		pb.columns(columns);
		return this;
	}

	@Override
    public InputDialogBuilder text(String id, String label, String dflt, Function<String,Optional<String>> validate) {
		pb.text(id, label, dflt, validate);
		return this;
	}

	@Override
	public InputDialogBuilder integer(String id, String label, Integer dflt, IntFunction<Optional<String>> validate) {
		pb.integer(id, label, dflt, validate);
		return this;
	}

    @Override
	public InputDialogBuilder decimal(String id, String label, Double dflt, DoubleFunction<Optional<String>> validate) {
		pb.decimal(id, label, dflt, validate);
		return this;
	}

    @Override
	public InputDialogBuilder checkBox(String id, String label, boolean dflt, String text) {
		pb.checkBox(id, label, dflt, text);
		return this;
	}

    @Override
	public <T> InputDialogBuilder comboBox(String id, String label, T dflt, Class<T> cls, Collection<T> items) {
		pb.comboBox(id, label, dflt, cls, items);
		return this;
	}

    @Override
	public <T> InputDialogBuilder radioList(String id, String label, T dflt, Class<T> cls, Collection<T> items) {
		pb.radioList(id, label, dflt, cls, items);
		return this;
	}
    
    @Override
    public InputDialogBuilder options(String id, String label, OptionValues dflt, Class<OptionValues> cls,
            OptionSet options) {
        pb.options(id, label, dflt, cls, options);
        return this;
    }
    
	@Override
	public InputDialog build() {
		InputDialog dlg = super.build();

		dlg.setDialogPane(pb.build());

		return dlg;
	}

}
