package com.dua3.fx.util.controls;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;

public class Buttons {

    public static ButtonBuilder button() {
        return new ButtonBuilder();
    }

    public static Node separator(Orientation orientation) {
        return new Separator(orientation);
    }
}
