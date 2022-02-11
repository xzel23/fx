package com.dua3.fx.icons;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.util.*;
import java.util.logging.Logger;

public final class IconUtil {
    private static final Logger LOG = Logger.getLogger(IconUtil.class.getName());

    private IconUtil() {
    }

    public static Optional<Icon> iconFromName(String name) {
        Class<IconProvider> iconProviderClass = IconProvider.class;
        return ServiceLoader.load(iconProviderClass)
                .stream()
                .peek(provider -> LOG.fine(() -> "found " + iconProviderClass.getName() + " implementation: " + provider.getClass().getName()))
                .map(provider -> provider.get().forName(name))
                .filter(Objects::nonNull)
                .findFirst();
    }

    public static Collection<String> iconProviderNames() {
        Class<IconProvider> iconProviderClass = IconProvider.class;
        return ServiceLoader.load(iconProviderClass)
                .stream()
                .map(p -> p.type().getName()).toList();
    }

    public static Icon emptyIcon() {
        return new EmptyIcon();
    }
}

class EmptyIcon extends Text implements Icon {

    private final IntegerProperty iconSize = new SimpleIntegerProperty();
    private final ObjectProperty<Paint> iconColor = new SimpleObjectProperty<>(Paint.valueOf("BLACK"));

    @Override
    public String getIconIdentifier() {
        return "";
    }

    @Override
    public int getIconSize() {
        return iconSize.get();
    }

    @Override
    public void setIconSize(int size) {
        iconSize.set(size);
    }

    @Override
    public IntegerProperty iconSizeProperty() {
        return iconSize;
    }

    @Override
    public Paint getIconColor() {
        return iconColor.get();
    }

    @Override
    public void setIconColor(Paint paint) {
        iconColor.set(paint);
    }

    @Override
    public ObjectProperty<Paint> iconColorProperty() {
        return iconColor;
    }

    @Override
    public Node node() {
        return this;
    }
}
