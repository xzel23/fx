package com.dua3.fx.icons;

import javafx.scene.Node;
import javafx.scene.control.Control;

import java.util.Optional;
import java.util.logging.Logger;

public class IconView extends Control {

    private static final Logger LOG = Logger.getLogger(IconView.class.getName());

    private String iconName = "";

    public IconView() {}

    public IconView(String iconName) {
        super();
        setIcon(iconName);
    }

    public void setIcon(String iconName) {
        Optional<Node> icon = IconUtil.iconFromName(iconName);
        icon.ifPresentOrElse(ic -> {
                    this.getChildren().setAll(ic);
                    this.iconName = iconName;
                },
                () -> {
                    this.getChildren().clear();
                    this.iconName = iconName + " (missing)";
                    LOG.warning("missing icon: "+iconName);
                }
        );
    }

    @Override
    public String toString() {
        return "icon[" + iconName + "]";
    }
}
