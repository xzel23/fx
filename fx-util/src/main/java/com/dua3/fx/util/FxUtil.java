package com.dua3.fx.util;

import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public final class FxUtil {

    public static Font convert(com.dua3.utility.text.Font font) {
        return new Font(font.getFamily(), font.getSizeInPoints());
    }

    private static javafx.geometry.Bounds boundsInLocal(String s, com.dua3.utility.text.Font f) {
        Text text = new Text(s);
        text.setFont(convert(f));
        return text.getBoundsInLocal();
    }

    public static Bounds getTextBounds(String s, com.dua3.utility.text.Font f) {
        return boundsInLocal(s,f);
    }

    public static double getTextWidth(String s, com.dua3.utility.text.Font f) {
        return boundsInLocal(s,f).getWidth();
    }

    public static double getTextHeight(String s, com.dua3.utility.text.Font f) {
        return boundsInLocal(s,f).getHeight();
    }

    public static Dimension2D growToFit(Dimension2D a, Bounds b) {
        return new Dimension2D(Math.max(a.getWidth(), b.getWidth()), Math.max(a.getHeight(), b.getHeight()));
    }
    
    private FxUtil() {}
}
