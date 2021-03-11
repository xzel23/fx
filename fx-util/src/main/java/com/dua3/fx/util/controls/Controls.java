package com.dua3.fx.util.controls;

import com.dua3.fx.icons.IconUtil;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;

public final class Controls {

    private Controls() {
    }

    /**
     * Create {@link ButtonBuilder} instance.
     * @return new ButtonBuilder
     */
    public static ButtonBuilder button() {
        return new ButtonBuilder();
    }

    /**
     * Create {@link SliderBuilder} instance.
     * @return new SliderBuilder
     */
    public static SliderBuilder slider() { return new SliderBuilder(); }

    /**
     * Create {@link javafx.scene.control.Separator}.
     * @param orientation the separator orientation
     * @return new {@link Separator}
     */
    public static Node separator(Orientation orientation) {
        return new Separator(orientation);
    }
    
    public static Node graphic(String name) {
        return IconUtil.iconFromName(name).orElseThrow(() -> new IllegalStateException("unknown icon: " + name)).node();
    }
}
