package com.dua3.fx.util;

import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JavaFX utility class.
 */
public final class FxUtil {

    private static class FontHelper {
        private static final List<String> FONTS_ALL;
        private static final List<String> FONTS_PROPORTIONAL;
        private static final List<String> FONTS_MONOSPACED;

        static {
            List<String> all = Font.getFamilies();
            all.sort(String::compareToIgnoreCase);

            List<String> proportional = new ArrayList<>();
            List<String> monospaced = new ArrayList<>();

            Text thin = new Text("1 l");
            Text thick = new Text("M_W");

            for (String family : all) {
                Font font = Font.font(family, FontWeight.NORMAL, FontPosture.REGULAR, 14.0d);
                thin.setFont(font);
                thick.setFont(font);
                if (thin.getLayoutBounds().getWidth() == thick.getLayoutBounds().getWidth()) {
                    monospaced.add(family);
                } else {
                    proportional.add(family);
                }
            }

            FONTS_ALL = Collections.unmodifiableList(all);
            FONTS_PROPORTIONAL = Collections.unmodifiableList(proportional);
            FONTS_MONOSPACED = Collections.unmodifiableList(monospaced);
        }
    }

    public enum FontType {
        PROPORTIONAL,
        MONOSPACE,
        ANY
    }
    
    public static List<String> getFonts(FontType t) {
        switch (t) {
            case ANY:
                return FontHelper.FONTS_ALL;
            case PROPORTIONAL:
                return FontHelper.FONTS_PROPORTIONAL;
            case MONOSPACE:
                return FontHelper.FONTS_MONOSPACED;
            default:
                throw new IllegalArgumentException(String.valueOf(t));
        }
    }

    public static Font convert(com.dua3.utility.text.Font font) {
        return Font.font(
                font.getFamily(), 
                font.isBold() ? FontWeight.BOLD : FontWeight.NORMAL, 
                font.isItalic() ? FontPosture.ITALIC : FontPosture.REGULAR, 
                font.getSizeInPoints());
    }

    /**
     * Convert {@link com.dua3.utility.data.Color} to {@link Color}.
     * @param color the color
     * @return the JavaFX color
     */
    public static Color convert(com.dua3.utility.data.Color color) {
        return Color.color(
                color.rf(),
                color.gf(),
                color.bf(),
                color.af()
        );
    }

    private static javafx.geometry.Bounds boundsInLocal(CharSequence s, com.dua3.utility.text.Font f) {
        Text text = new Text(s.toString());
        text.setFont(convert(f));
        return text.getBoundsInLocal();
    }

    public static Bounds getTextBounds(CharSequence s, com.dua3.utility.text.Font f) {
        return boundsInLocal(s,f);
    }

    public static double getTextWidth(CharSequence s, com.dua3.utility.text.Font f) {
        return boundsInLocal(s,f).getWidth();
    }

    public static double getTextHeight(CharSequence s, com.dua3.utility.text.Font f) {
        return boundsInLocal(s,f).getHeight();
    }

    public static Dimension2D growToFit(Dimension2D a, Bounds b) {
        return new Dimension2D(Math.max(a.getWidth(), b.getWidth()), Math.max(a.getHeight(), b.getHeight()));
    }

    private FxUtil() {}
}
