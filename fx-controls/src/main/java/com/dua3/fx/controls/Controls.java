package com.dua3.fx.controls;

import com.dua3.fx.icons.Icon;
import com.dua3.fx.icons.IconUtil;
import com.dua3.fx.util.FxUtil;
import com.dua3.utility.data.Color;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;

import java.util.function.BiFunction;

public final class Controls {

    private Controls() {
    }

    /**
     * Create {@link ButtonBuilder} instance for standard buttons.
     *
     * @return new ButtonBuilder
     */
    public static ButtonBuilder<Button> button() {
        return ButtonBuilder.builder(Button::new);
    }

    /**
     * Create {@link ButtonBuilder} instance for toggle buttons.
     *
     * @return new ButtonBuilder
     */
    public static ButtonBuilder<ToggleButton> toggleButton() {
        return ButtonBuilder.builder(ToggleButton::new);
    }

    /**
     * Create {@link ButtonBuilder} instance for toggle buttons.
     *
     * @param selected the initial selection state of the button
     * @return new ButtonBuilder
     */
    public static ButtonBuilder<ToggleButton> toggleButton(boolean selected) {
        return ButtonBuilder.builder(() -> {
            ToggleButton b = new ToggleButton();
            b.setSelected(selected);
            return b;
        });
    }

    /**
     * Create {@link SliderBuilder} instance.
     *
     * @return new SliderBuilder
     */
    public static SliderBuilder slider() {
        return new SliderBuilder(SliderWithButtons.Mode.SLIDER_ONLY, (v,t) -> "");
    }

    /**
     * Create {@link SliderBuilder} instance.
     *
     * @param mode the {@link SliderWithButtons.Mode}
     * @param formatter the formatter that generates the label text; first argument is current value and second is max value
     * @return new SliderBuilder
     */
    public static SliderBuilder slider(SliderWithButtons.Mode mode, BiFunction<Double, Double, String> formatter) {
        return new SliderBuilder(mode, formatter);
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

    /**
     * Get graphic for an icon by icon name.
     * @param name the icon name
     * @return a node for the graphic
     * @throws IllegalStateException if no icon with a matching name is found
     * @see IconUtil#iconFromName(String) 
     */
    public static Node graphic(String name) {
        return IconUtil.iconFromName(name).orElseThrow(() -> new IllegalStateException("unknown icon: " + name)).node();
    }

    /**
     * Get graphic for an icon by icon name.
     * @param name the icon name
     * @param size the requested size
     * @return a node for the graphic
     * @throws IllegalStateException if no icon with a matching name is found
     * @see IconUtil#iconFromName(String) 
     */
    public static Node graphic(String name, int size) {
        Icon icon = IconUtil.iconFromName(name).orElseThrow(() -> new IllegalStateException("unknown icon: " + name));
        icon.setIconSize(size);
        return icon.node();
    }

    /**
     * Get graphic for an icon by icon name.
     * @param name the icon name
     * @param size the requested size
     * @param paint the {@link Paint} to use
     * @return a node for the graphic
     * @throws IllegalStateException if no icon with a matching name is found
     * @see IconUtil#iconFromName(String)
     */
    public static Node graphic(String name, int size, Paint paint) {
        Icon icon = IconUtil.iconFromName(name).orElseThrow(() -> new IllegalStateException("unknown icon: " + name));
        icon.setIconSize(size);
        icon.setIconColor(paint);
        return icon.node();
    }

    /**
     * Get graphic for an icon by icon name.
     * @param name the icon name
     * @param size the requested size
     * @param color the {@link Color} to use
     * @return a node for the graphic
     * @throws IllegalStateException if no icon with a matching name is found
     * @see IconUtil#iconFromName(String)
     */
    public static Node graphic(String name, int size, Color color) {
        Icon icon = IconUtil.iconFromName(name).orElseThrow(() -> new IllegalStateException("unknown icon: " + name));
        icon.setIconSize(size);
        icon.setIconColor(FxUtil.convert(color));
        return icon.node();
    }

    /**
     * Make a region resizable by dragging its edge.
     * @param region the region
     * @param borders the borders to make draggable               
     */
    public static void makeResizable(Region region, Border... borders) {
        DragResizer.makeResizable(region, 6, borders);
    }

    /**
     * Make a region resizable by dragging its edge.
     * @param region the region
     * @param resizeMargin size of the draggable margin
     * @param borders the borders to make draggable               
     */
    public static void makeResizable(Region region, int resizeMargin, Border... borders) {
        DragResizer.makeResizable(region, resizeMargin, borders);
    }
}
