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

package com.dua3.fx.web;

import com.dua3.fx.util.Dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.web.WebEngine;

public class WebViews {
    private WebViews() {
        // utility class
    }

    public static void setConfirmationHandler(WebEngine engine) {
        engine.setConfirmHandler(s -> Dialogs.confirmation().header("%s", s).buttons(ButtonType.YES, ButtonType.NO)
                .defaultButton(ButtonType.NO).showAndWait().filter(b -> b.equals(ButtonType.YES)).isPresent());
    }

    public static void setAlertHandler(WebEngine engine) {
        engine.setOnAlert(e -> Dialogs.warning().header("%s", e.getData()).showAndWait());
    }

    public static void setPromptHandler(WebEngine engine) {
        engine.setPromptHandler(p -> Dialogs.prompt().header("%s", p.getMessage())
                .defaultValue("%s", p.getDefaultValue()).showAndWait().orElse(""));
    }

}