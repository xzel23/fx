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

import javafx.scene.control.Alert.AlertType;

/** 
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts. 
 */
public class AlertPaneBuilder 
extends AbstractPaneBuilder<InputDialogPane<Void>, AlertPaneBuilder, Void> {
	private String text = null;

	public AlertPaneBuilder(AlertType type) {
		super();
		setDialogSupplier(() -> createPane(type));
	}

	public AlertPaneBuilder text(String fmt, Object... args) {
		this.text = format(fmt, args);
		return this;
	}

	private static InputDialogPane<Void> createPane(AlertType type) {
		return new InputDialogPane<>() {
			@Override
			public Void get() {
				return null;
			}

			@Override
			public void init() {
				// nop
			}
		};
	}

	@Override
	public InputDialogPane<Void> build() {
		InputDialogPane<Void> inputPane = super.build();
		applyIfNotNull((pane,text) -> pane.setContentText(text), inputPane, text);
		return inputPane;
	}
}
