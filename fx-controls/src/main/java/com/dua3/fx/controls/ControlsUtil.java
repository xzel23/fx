package com.dua3.fx.controls;

import javafx.scene.Node;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

public class ControlsUtil {

    public static Node iconFromName(String name) {
        int idx = name.lastIndexOf('.');
        String pack = name.substring(0, idx);
        String iconName = name.substring(idx + 1);

        try {
            Ikon ikon = (Ikon) Class.forName(pack).getDeclaredField(iconName).get(null);
            return new FontIcon(ikon);
        } catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException e) {
            throw new IllegalStateException("could not load icon: " + name, e);
        }
    }

}
