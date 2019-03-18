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

import com.dua3.utility.options.OptionSet;
import com.dua3.utility.options.OptionValues;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts.
 */
public class InputDialogBuilder
extends AbstractDialogBuilder<InputDialog, InputDialogBuilder, Map<String, Object>> 
implements InputBuilder<InputDialogBuilder> {

	private final InputPaneBuilder pb = new InputPaneBuilder();
	
	public InputDialogBuilder() {
		setDialogSupplier(this::createDialog);
	}

	private InputDialog createDialog() {
		InputDialog dlg = new InputDialog();
		InputPane dialogPane = pb.build();
		dialogPane.init();

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		final Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
		okButton.disableProperty().bind(Bindings.not(dialogPane.validProperty()));

		dlg.setDialogPane(dialogPane);
		return dlg;
	}

    @Override
    public <T> InputDialogBuilder add(String id, String label, Class<T> type, Supplier<T> dflt, InputControl<T> control) {
        pb.add(id, label, type, dflt, control);
        return this;
    }

	@Override
    public <T> InputDialogBuilder add(String id, Class<T> type, Supplier<T> dflt, InputControl<T> control) {
        pb.add(id, type, dflt, control);
        return this;
    }

	@Override
    public InputDialogBuilder columns(int columns) {
		pb.columns(columns);
		return this;
	}

	@Override
    public InputDialogBuilder string(String id, String label, Supplier<String> dflt, Function<String,Optional<String>> validate) {
		pb.string(id, label, dflt, validate);
		return this;
	}

	@Override
	public InputDialogBuilder integer(String id, String label, Supplier<Integer> dflt, Function<Integer,Optional<String>> validate) {
		pb.integer(id, label, dflt, validate);
		return this;
	}

    @Override
	public InputDialogBuilder decimal(String id, String label, Supplier<Double> dflt, Function<Double,Optional<String>> validate) {
		pb.decimal(id, label, dflt, validate);
		return this;
	}

    @Override
	public InputDialogBuilder checkBox(String id, String label, Supplier<Boolean> dflt, String text) {
		pb.checkBox(id, label, dflt, text);
		return this;
	}

    @Override
	public <T> InputDialogBuilder comboBox(String id, String label, Supplier<T> dflt, Class<T> cls, Collection<T> items) {
		pb.comboBox(id, label, dflt, cls, items);
		return this;
	}

    @Override
	public <T> InputDialogBuilder radioList(String id, String label, Supplier<T> dflt, Class<T> cls, Collection<T> items) {
		pb.radioList(id, label, dflt, cls, items);
		return this;
	}

	@Override
	public InputDialogBuilder options(String id, String label, Supplier<OptionValues> dflt, Supplier<OptionSet> options) {
		pb.options(id, label, dflt, options);
		return this;
	}

	@Override
	public InputDialogBuilder options(String id, Supplier<OptionValues> dflt, Supplier<OptionSet> options) {
		pb.options(id, dflt, options);
		return this;
	}

}
