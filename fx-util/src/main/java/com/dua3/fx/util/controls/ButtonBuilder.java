package com.dua3.fx.util.controls;

import com.dua3.fx.icons.IconUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

public class ButtonBuilder {
    private String text = null;
    private Node graphic = null;
    private String tooltip = null;
    private EventHandler<ActionEvent> action = null;

    public ButtonBuilder text(String text) {
        this.text = text;
        return this;
    }

    public ButtonBuilder graphic(Node graphic) {
        this.graphic = graphic;
        return this;
    }

    public ButtonBuilder graphic(String name) {
        this.graphic = IconUtil.iconFromName(name);
        return this;
    }

    public ButtonBuilder tooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public ButtonBuilder action(EventHandler<ActionEvent> action) {
        this.action = action;
        return this;
    }

    public ButtonBuilder action(Runnable action) {
        this.action = evt -> action.run();
        return this;
    }

    public Button build() {
        Button button = new Button();

        if (text != null) {
            button.setText(text);
        }
        if (graphic != null) {
            button.setGraphic(graphic);
        }
        if (tooltip != null) {
            button.setTooltip(new Tooltip(tooltip));
        }
        if (action != null) {
            button.setOnAction(action);
        }

        return button;
    }
}