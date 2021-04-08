package com.dua3.fx.util;

import com.dua3.utility.io.IOUtil;
import com.dua3.utility.math.AffineTransformation;
import com.dua3.utility.text.FontDef;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

/**
 * JavaFX utility class.
 */
public final class FxUtil {

    public static String asText(URI uri) {
        return uri==null ? "" : URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8);
    }

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

    /**
     * List available fonts for a given {@link FontType}.
     * @param t the font type
     * @return list of fonts
     */
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

    /**
     * Convert {@link com.dua3.utility.text.Font} to JavaFX {@link Font}.
     * @param font the font
     * @return the JavaFX Font
     */
    public static Font convert(com.dua3.utility.text.Font font) {
        return Font.font(
                font.getFamily(), 
                font.isBold() ? FontWeight.BOLD : FontWeight.NORMAL, 
                font.isItalic() ? FontPosture.ITALIC : FontPosture.REGULAR, 
                font.getSizeInPoints());
    }

    /**
     * Convert JavaFX {@link Font} to {@link FontDef}.
     * @param font the font
     * @return the FontDef
     */
    public static com.dua3.utility.text.FontDef toFontDef(Font font) {
        FontDef fd = new com.dua3.utility.text.FontDef();
        fd.setFamily(font.getFamily());
        fd.setSize((float) font.getSize());
        return fd;
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

    /**
     * Convert {@link Color to }{@link com.dua3.utility.data.Color}.
     * @param color the JavaFX color
     * @return the color
     */
    public static com.dua3.utility.data.Color convert(Color color) {
        return new com.dua3.utility.data.Color(
                (int) Math.round(color.getRed()/255.0),
                (int) Math.round(color.getGreen()/255.0),
                (int) Math.round(color.getBlue()/255.0),
                (int) Math.round(color.getOpacity()/255.0)
        );
    }

    /**
     * Convert {@link AffineTransformation} to JavaFX {@link Affine}.
     * @param at the affine transformation
     * @return the JavaFX affine transformation
     */
    public static Affine convert(AffineTransformation at) {
        return new Affine(at.a(), at.c(), at.e(), at.b(), at.d(), at.f());
    }

    /**
     * Convert JavaFX {@link Affine} to {@link AffineTransformation}.
     * @param a the JavaFX affine transformation
     * @return the affine transformation
     */
    public static AffineTransformation convert(Affine a) {
        return new AffineTransformation(a.getMxx(), a.getMxy(), a.getMyx(), a.getMyy(), a.getTx(), a.getTy());
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

    /**
     * Test if filename matches filter.
     * @param filter the filter
     * @param filename the filename
     * @return true if filename matches filter
     */
    public static boolean matches(FileChooser.ExtensionFilter filter, String filename) {
        String fext = IOUtil.getExtension(filename).toLowerCase(Locale.ROOT);
        return filter.getExtensions().stream()
                .map(ext -> ext.replaceFirst("^\\*\\.", "").toLowerCase(Locale.ROOT))    
                .anyMatch(ext -> Objects.equals(ext, fext));
    }

    /**
     * Test if file matches filter.
     * @param filter the filter
     * @param file the file
     * @return true if filename matches filter
     */
    public static boolean matches(FileChooser.ExtensionFilter filter, Path file) {
        return matches(filter, file.toString());
    }

    /**
     * Test if filename matches filter.
     * @param filter the filter
     * @param file the file
     * @return true if file matches filter
     */
    public static boolean matches(FileChooser.ExtensionFilter filter, File file) {
        return matches(filter, file.getName());
    }

    /**
     * Test if URI matches filter.
     * @param filter the filter
     * @param uri the URI
     * @return true if file matches filter
     */
    public static boolean matches(FileChooser.ExtensionFilter filter, URI uri) {
        return matches(filter, uri.getPath());
    }

    private FxUtil() {}
}
