package com.dua3.fx.icons;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.paint.Paint;

public interface Icon extends Styleable {
    void setIconSize(int size);

    int getIconSize();

    IntegerProperty iconSizeProperty();

    void setIconColor(Paint paint);

    Paint getIconColor();

    Property<Paint> iconColorProperty();

    Node node();
}
