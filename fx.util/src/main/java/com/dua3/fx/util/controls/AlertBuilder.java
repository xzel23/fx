// Copyright 2019 Axel Howind
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.dua3.fx.util.controls;

import java.util.Arrays;
import java.util.Objects;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

/**
 * Builder for Alert Dialogs.
 * Provides a fluent interface to create Alerts.
 */
public class AlertBuilder
    extends AbstractDialogBuilder<Alert, AlertBuilder, ButtonType> {
  public AlertBuilder(AlertType type) {
    super(() -> new Alert(type));
  }

  private ButtonType[] buttons;
  private ButtonType defaultButton;

  /**
   * Create Alert instance.
   * 
   * @return Alert instance
   */
  @Override
  public Alert build() {
    Alert dlg = super.build();

    if (buttons != null) {
      dlg.getButtonTypes().setAll(buttons);
    }

    if (defaultButton != null) {
      DialogPane pane = dlg.getDialogPane();
      for (ButtonType t : dlg.getButtonTypes()) {
        ((Button) pane.lookupButton(t)).setDefaultButton(t == defaultButton);
      }
    }

    return dlg;
  }

  /**
   * Define Alert Buttons.
   * 
   * @param buttons
   *                the buttons to show
   * @return
   *         {@code this}
   */
  public AlertBuilder buttons(ButtonType... buttons) {
    this.buttons = Arrays.copyOf(buttons, buttons.length);
    return this;
  }

  /**
   * Define the default Buttons.
   * 
   * @param button
   *               the button to use as default
   * @return
   *         {@code this}
   */
  public AlertBuilder defaultButton(ButtonType button) {
    this.defaultButton = Objects.requireNonNull(button);
    return this;
  }

}
