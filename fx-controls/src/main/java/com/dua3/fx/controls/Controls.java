package com.dua3.fx.controls;

import com.dua3.fx.icons.Icon;
import com.dua3.fx.icons.IconUtil;
import com.dua3.fx.util.FxUtil;
import com.dua3.utility.data.Color;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;

public final class Controls {

    private Controls() {
    }

    /**
     * Create {@link ButtonBuilder} instance.
     *
     * @return new ButtonBuilder
     */
    public static ButtonBuilder button() {
        return new ButtonBuilder();
    }

    /**
     * Create {@link SliderBuilder} instance.
     *
     * @return new SliderBuilder
     */
    public static SliderBuilder slider() {
        return new SliderBuilder();
    }

    /**
     * Create {@link javafx.scene.control.Separator}.
     *
     * @param orientation the separator orientation
     * @return new {@link Separator}
     */
    public static Node separator(Orientation orientation) {
        return new Separator(orientation);
    }

    public static Node graphic(String name) {
        return IconUtil.iconFromName(name).orElseThrow(() -> new IllegalStateException("unknown icon: " + name)).node();
    }

    public static Node graphic(String name, int size) {
        Icon icon = IconUtil.iconFromName(name).orElseThrow(() -> new IllegalStateException("unknown icon: " + name));
        icon.setIconSize(size);
        return icon.node();
    }

    public static Node graphic(String name, int size, Paint paint) {
        Icon icon = IconUtil.iconFromName(name).orElseThrow(() -> new IllegalStateException("unknown icon: " + name));
        icon.setIconSize(size);
        icon.setIconColor(paint);
        return icon.node();
    }

    public static Node graphic(String name, int size, Color color) {
        Icon icon = IconUtil.iconFromName(name).orElseThrow(() -> new IllegalStateException("unknown icon: " + name));
        icon.setIconSize(size);
        icon.setIconColor(FxUtil.convert(color));
        return icon.node();
    }

    public static void makeResizable(Region region, Border... borders) {
        DragResizer.makeResizable(region, 6, borders);
    }

    public static void makeResizable(Region region, int resizeMargin, Border... borders) {
        DragResizer.makeResizable(region, resizeMargin, borders);
    }
}
