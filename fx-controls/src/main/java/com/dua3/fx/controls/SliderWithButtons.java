package com.dua3.fx.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

public class SliderWithButtons extends Region {
    private Pane pane;
    private final Slider slider;
    private final Button btnIncrement;
    private final Button btnDecrement;
    private final List<Node> children = new ArrayList<>();
    
    SliderWithButtons() {
        this.slider = new Slider();
        this.btnDecrement = new Button("-");
        this.btnIncrement = new Button("+");

        if (btnDecrement!=null) {
            btnDecrement.setOnAction(evt -> slider.decrement());
            btnDecrement.setFocusTraversable(false);
            children.add(btnDecrement);
        }

        children.add(slider);

        if (btnIncrement!=null) {
            btnIncrement.setOnAction(evt -> slider.increment());
            btnIncrement.setFocusTraversable(false);
            children.add(btnIncrement);
        }

        initPane();
    }
    
    public void setOrientation(Orientation orientation) {
        if (orientation!=slider.getOrientation()) {
            slider.setOrientation(orientation);
            initPane();
        }
    }

    private void initPane() {
        pane = box(slider.getOrientation());
        pane.getChildren().addAll(children);
        getChildren().setAll(pane);
    }

    private Pane box(Orientation orientation) {
        if (orientation==Orientation.HORIZONTAL) {
            HBox box = new HBox();
            box.setAlignment(Pos.CENTER);
            return box;
        } else {
            VBox box = new VBox();
            box.setAlignment(Pos.CENTER);
            return box;
        }
    }

    public void setDecrementText(String value) {
        btnDecrement.setText(value);
    }

    public void setDecrementGraphic(Node value) {
        btnDecrement.setGraphic(value);
    }

    public void setIncrementText(String value) {
        btnIncrement.setText(value);
    }

    public void setIncrementGraphic(Node value) {
        btnIncrement.setGraphic(value);
    }
    
    public void setMin(double value) {
        slider.setMin(value);
    }
    
    public void setMax(double value) {
        slider.setMax(value);
    }
    
    public void setValue(double value) {
        slider.setValue(value);
    }
    
    public void setBlockIncrement(double value) {
        slider.setBlockIncrement(value);
    }
    
    public void setShowTickLabels(boolean value) {
        slider.setShowTickLabels(value);
    }
    
    public void setShowTickMarks(boolean value) {
        slider.setShowTickMarks(value);
    }
    
    public double getMax() {
        return slider.getMax();
    }
    
    public double getMin() {
        return slider.getMin();
    }

    public double getValue() {
        return slider.getValue();
    }

    public double getMajorTickUnit() {
        return slider.getMajorTickUnit();
    }

    public double getMinorTickCount() {
        return slider.getMinorTickCount();
    }

    public double getBlockIncrement() {
        return slider.getBlockIncrement();
    }

    public DoubleProperty valueProperty() {
        return slider.valueProperty();
    }

    public DoubleProperty minProperty() {
        return slider.minProperty();
    }

    public DoubleProperty maxProperty() {
        return slider.maxProperty();
    }

    public DoubleProperty majorTickUnitProperty() {
        return slider.majorTickUnitProperty();
    }

    public IntegerProperty minorTickCountProperty() {
        return slider.minorTickCountProperty();
    }

    public BooleanProperty valueChangingProperty() {
        return slider.valueChangingProperty();
    }

    public ObjectProperty<StringConverter<Double>> labelFormatterProperty() {
        return slider.labelFormatterProperty();
    }

    public BooleanProperty showTickLabelsProperty() {
        return slider.showTickLabelsProperty();
    }

    public BooleanProperty showTickMarksProperty() {
        return slider.showTickMarksProperty();
    }

    public BooleanProperty snapToTicksProperty() {
        return slider.snapToTicksProperty();
    }

}
