package com.dua3.fx.controls;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Tooltip;

import java.util.Objects;
import java.util.function.Supplier;

public class ButtonBuilder<B extends ButtonBase> {
    private final Supplier<B> factory;
    private String text = null;
    private Node graphic = null;
    private String tooltip = null;
    private EventHandler<ActionEvent> action = null;

    /**
     * Constructor.
     *
     * @param factory the factory method for Button instances
     */
    ButtonBuilder(Supplier<B> factory) {
        this.factory = Objects.requireNonNull(factory);
    }

    /**
     * Set text for the button.
     *
     * @param text the text
     * @return this ButtonBuilder instance
     */
    public ButtonBuilder<B> text(String text) {
        this.text = text;
        return this;
    }

    /**
     * Set graphic for the button.
     *
     * @param graphic the graphic to use
     * @return this ButtonBuilder instance
     */
    public ButtonBuilder<B> graphic(Node graphic) {
        this.graphic = graphic;
        return this;
    }

    /**
     * Set tooltip for the button.
     *
     * @param tooltip the tooltip text
     * @return this ButtonBuilder instance
     */
    public ButtonBuilder<B> tooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    /**
     * Set event handler for the button.
     *
     * @param action the {@link EventHandler}
     * @return this ButtonBuilder instance
     */
    public ButtonBuilder<B> action(EventHandler<ActionEvent> action) {
        this.action = action;
        return this;
    }

    /**
     * Set action for the button.
     *
     * @param action the action to perform when pressed
     * @return this ButtonBuilder instance
     */
    public ButtonBuilder<B> action(Runnable action) {
        this.action = evt -> action.run();
        return this;
    }

    /**
     * Build the button.
     *
     * @return new button instance
     */
    public B build() {
        B button = factory.get();

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
