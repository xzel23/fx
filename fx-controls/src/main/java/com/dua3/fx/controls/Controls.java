package com.dua3.fx.controls;

import com.dua3.fx.icons.Icon;
import com.dua3.fx.icons.IconUtil;
import com.dua3.fx.icons.IconView;
import com.dua3.fx.util.FxUtil;
import com.dua3.utility.data.Color;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
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
        return new ButtonBuilder<>(Button::new);
    }

    /**
     * Create {@link ButtonBuilder} instance for toggle buttons.
     *
     * @return new ButtonBuilder
     */
    public static ButtonBuilder<ToggleButton> toggleButton() {
        return new ButtonBuilder<>(ToggleButton::new);
    }

    /**
     * Create {@link ButtonBuilder} instance for toggle buttons.
     *
     * @param selected the initial selection state of the button
     * @return new ButtonBuilder
     */
    public static ButtonBuilder<ToggleButton> toggleButton(boolean selected) {
        return new ButtonBuilder<>(() -> {
            ToggleButton b = new ToggleButton();
            b.setSelected(selected);
            return b;
        });
    }

    /**
     * Create {@link ButtonBuilder} instance for checkboxes.
     *
     * @return new ButtonBuilder
     */
    public static ButtonBuilder<CheckBox> checkbox() {
        return new ButtonBuilder<>(CheckBox::new);
    }

    /**
     * Create {@link ButtonBuilder} instance for checkboxes.
     *
     * @param selected the initial selection state of the button
     * @return new ButtonBuilder
     */
    public static ButtonBuilder<CheckBox> checkbox(boolean selected) {
        return new ButtonBuilder<>(() -> {
            CheckBox b = new CheckBox();
            b.setSelected(selected);
            return b;
        });
    }

    /**
     * Create a {@link FileInputBuilder}.
     *
     * @return new FileInputBuilder
     */
    public static FileInputBuilder fileInput(FileDialogMode mode) {
        return new FileInputBuilder(mode);
    }

    /**
     * Create {@link SliderBuilder} instance.
     *
     * @return new SliderBuilder
     */
    public static SliderBuilder slider() {
        return new SliderBuilder(SliderWithButtons.Mode.SLIDER_ONLY, (v, t) -> "");
    }

    /**
     * Create {@link SliderBuilder} instance.
     *
     * @param mode      the {@link SliderWithButtons.Mode}
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
     *
     * @param name the icon name
     * @return a node for the graphic
     * @throws IllegalStateException if no icon with a matching name is found
     * @see IconUtil#iconFromName(String)
     */
    public static Node graphic(String name) {
        return icon(name).node();
    }

    /**
     * Get icon by name.
     *
     * @param name the icon name
     * @return icon
     * @throws IllegalStateException if no icon with a matching name is found
     * @see IconUtil#iconFromName(String)
     */
    public static Icon icon(String name) {
        return IconUtil.iconFromName(name).orElseThrow(() -> new IllegalStateException("unknown icon: " + name));
    }

    /**
     * Get graphic for an icon by icon name.
     *
     * @param name the icon name
     * @param size the requested size
     * @return a node for the graphic
     * @throws IllegalStateException if no icon with a matching name is found
     * @see IconUtil#iconFromName(String)
     */
    public static Node graphic(String name, int size) {
        Icon icon = icon(name);
        icon.setIconSize(size);
        return icon.node();
    }

    /**
     * Get graphic for an icon by icon name.
     *
     * @param name  the icon name
     * @param size  the requested size
     * @param paint the {@link Paint} to use
     * @return a node for the graphic
     * @throws IllegalStateException if no icon with a matching name is found
     * @see IconUtil#iconFromName(String)
     */
    public static Node graphic(String name, int size, Paint paint) {
        Icon icon = icon(name);
        icon.setIconSize(size);
        icon.setIconColor(paint);
        return icon.node();
    }

    /**
     * Get graphic for an icon by icon name.
     *
     * @param name  the icon name
     * @param size  the requested size
     * @param color the {@link Color} to use
     * @return a node for the graphic
     * @throws IllegalStateException if no icon with a matching name is found
     * @see IconUtil#iconFromName(String)
     */
    public static Node graphic(String name, int size, Color color) {
        return graphic(name, size, FxUtil.convert(color));
    }

    /**
     * Create an Icon with a tooltip.
     *
     * @param name  the icon name
     * @param size  the requested size
     * @param paint the {@link Paint} to use
     * @param tooltipText the text to display as tooltip
     * @return a node for the graphic
     * @throws IllegalStateException if no icon with a matching name is found
     * @see IconUtil#iconFromName(String)
     */
    public static Node tooltipIcon(String name, int size, Paint paint, String tooltipText) {
        IconView iv = new IconView(name, size, paint);
        if (!tooltipText.isBlank()) {
            iv.setTooltip(new Tooltip(tooltipText));
        }
        return iv;
    }

    /**
     * Create an Icon with a tooltip.
     *
     * @param name  the icon name
     * @param size  the requested size
     * @param color the {@link Color} to use
     * @param tooltipText the text to display as tooltip
     * @return a node for the graphic
     * @throws IllegalStateException if no icon with a matching name is found
     * @see IconUtil#iconFromName(String)
     */
    public static Node tooltipIcon(String name, int size, Color color, String tooltipText) {
        return tooltipIcon(name, size, FxUtil.convert(color), tooltipText);
    }

    /**
     * Get TextFieldBuilder for creating a TextField.
     * @return TextFieldBuilder instance
     */
    public static TextFieldBuilder textField() {
        return new TextFieldBuilder();
    }

    /**
     * Make a region resizable by dragging its edge.
     *
     * @param region  the region
     * @param borders the borders to make draggable
     */
    public static void makeResizable(Region region, Border... borders) {
        DragResizer.makeResizable(region, 6, borders);
    }

    /**
     * Make a region resizable by dragging its edge.
     *
     * @param region       the region
     * @param resizeMargin size of the draggable margin
     * @param borders      the borders to make draggable
     */
    public static void makeResizable(Region region, int resizeMargin, Border... borders) {
        DragResizer.makeResizable(region, resizeMargin, borders);
    }

    /**
     * Create new {@link Menu}.
     *
     * @param text    the text to show
     * @param items   the menu items
     * @return new menu
     */
    public static Menu menu(String text, MenuItem... items) {
        return new Menu(text, null, items);
    }

    /**
     * Create new {@link Menu}.
     *
     * @param text    the text to show
     * @param graphic the graphic to show before the text
     * @param items   the menu items
     * @return new menu
     */
    public static Menu menu(String text, Node graphic, MenuItem... items) {
        return new Menu(text, graphic, items);
    }

    /**
     * Create new {@link MenuItem}.
     *
     * @param text    the text to show
     * @param graphic the graphic to show before the text
     * @param action  the action to perform when the menu item is invoked
     * @return new menu item
     */
    public static MenuItem menuItem(String text, Node graphic, Runnable action) {
        return menuItem(text, graphic, action, true);
    }

    /**
     * Create new {@link MenuItem}.
     *
     * @param text    the text to show
     * @param graphic the graphic to show before the text
     * @param enabled the enabled state
     * @param action  the action to perform when the menu item is invoked
     * @return new menu item
     */
    public static MenuItem menuItem(String text, Node graphic, Runnable action, boolean enabled) {
        MenuItem mi = new MenuItem(text, graphic);
        mi.setDisable(!enabled);
        mi.setOnAction(evt -> action.run());
        return mi;
    }

    /**
     * Create new {@link MenuItem}.
     *
     * @param text   the text to show
     * @param action the action to perform when the menu item is invoked
     * @return new menu item
     */
    public static MenuItem menuItem(String text, Runnable action) {
        return menuItem(text, action, true);
    }

    /**
     * Create new {@link MenuItem}.
     *
     * @param text    the text to show
     * @param enabled the enabled state
     * @param action  the action to perform when the menu item is invoked
     * @return new menu item
     */
    public static MenuItem menuItem(String text, Runnable action, boolean enabled) {
        MenuItem mi = new MenuItem(text);
        mi.setDisable(!enabled);
        mi.setOnAction(evt -> action.run());
        return mi;
    }
}
