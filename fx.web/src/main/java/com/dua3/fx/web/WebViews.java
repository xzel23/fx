
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