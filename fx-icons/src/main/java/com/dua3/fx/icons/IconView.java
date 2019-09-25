package com.dua3.fx.icons;

import javafx.scene.Node;
import javafx.scene.control.Control;

import java.util.Objects;
import java.util.Optional;

public class IconView extends Control {

    private Icon icon;

    public IconView() {
    }

    public IconView(String iconName) {
        super();
        setIcon(iconName);
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        if (icon == null) {
            this.getChildren().clear();
        } else {
            this.getChildren().setAll(icon.node());
        }
    }

    public void setIcon(String iconName) {
        Optional<Icon> icon = IconUtil.iconFromName(iconName);
        setIcon(icon.orElse(null));
    }

    @Override
    public String toString() {
        return Objects.toString(icon);
    }

    @Override
    public Node getStyleableNode() {
        return icon.getStyleableNode();
    }

    public Icon getIcon() {
        return icon;
    }

}
