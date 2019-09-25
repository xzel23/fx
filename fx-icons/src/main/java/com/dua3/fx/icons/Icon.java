package com.dua3.fx.icons;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.paint.Paint;

public interface Icon extends Styleable {
    String getIconIdentifier();

    int getIconSize();

    void setIconSize(int size);

    IntegerProperty iconSizeProperty();

    Paint getIconColor();

    void setIconColor(Paint paint);

    ObjectProperty<Paint> iconColorProperty();

    Node node();
}
