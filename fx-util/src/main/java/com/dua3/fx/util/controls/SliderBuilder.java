package com.dua3.fx.util.controls;

import com.dua3.fx.icons.IconUtil;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableNumberValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

import java.util.function.DoubleConsumer;

public class SliderBuilder {
    private final SliderWithButtons slider;

    public SliderBuilder() {
        slider = new SliderWithButtons();
    }
    
    public SliderBuilder orientation(Orientation value) {
        slider.setOrientation(value);
        return this;
    }
    
    public SliderBuilder min(double value) {
        slider.setMin(value);
        return this;
    }
    
    public SliderBuilder max(double value) {
        slider.setMax(value);
        return this;
    }
    
    public SliderBuilder value(double value) {
        slider.setValue(value);
        return this;
    }
    
    public SliderBuilder incrementText(String value) {
        slider.setIncrementText(value);
        return this;
    }
    
    public SliderBuilder incrementGraphic(Node value) {
        slider.setIncrementGraphic(value);
        return this;
    }
    
    public SliderBuilder decrementText(String value) {
        slider.setDecrementText(value);
        return this;
    }
    
    public SliderBuilder decrementGraphic(Node value) {
        slider.setDecrementGraphic(value);
        return this;
    }
    
    public SliderBuilder blockIncrement(double value) {
        slider.setBlockIncrement(value);
        return this;
    }
    
    public SliderBuilder showTickLabels(boolean value) {
        slider.setShowTickLabels(value);
        return this;
    }
    
    public SliderBuilder showTickMarks(boolean value) {
        slider.setShowTickMarks(value);
        return this;
    }
    
    public SliderBuilder onChange(DoubleConsumer onChange) {
        slider.valueProperty().addListener((v,o,n) -> onChange.accept(n.doubleValue()));
        return this;
    }

    public SliderBuilder bindMin(ObservableNumberValue value) {
        slider.minProperty().bind(value);
        return this;
    }

    public SliderBuilder bindMax(ObservableNumberValue value) {
        slider.maxProperty().bind(value);
        return this;
    }

    public SliderWithButtons build() {
        return slider;
    }
}
