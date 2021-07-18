package com.dua3.fx.util;

import com.dua3.utility.io.IOUtil;
import com.dua3.utility.math.geometry.AffineTransformation2d;
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
import java.io.IOException;
import java.io.InputStream;
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

    /**
     * Load fonts from InputStream.
     * @param in the stream to read font data from.
     * @return list of fonts, if no font could be read the empty list is returned
     * @throws IOException if an error occurs
     */
    public static List<com.dua3.utility.text.Font> loadFonts(InputStream in) throws IOException {
        try (in) {
            Font[] fxFonts = Font.loadFonts(in, 0);
            if (fxFonts==null) {
                return Collections.emptyList();
            }

            List<com.dua3.utility.text.Font> fonts = new ArrayList<>(fxFonts.length);
            for (Font fxFont: fxFonts) {
                String style = fxFont.getStyle().toLowerCase(Locale.ROOT);
                com.dua3.utility.text.Font font = new com.dua3.utility.text.Font(
                        fxFont.getFamily(),
                        (float) fxFont.getSize(),
                        com.dua3.utility.data.Color.BLACK,
                        style.contains("bold"),
                        style.contains("italic") || style.contains("oblique"),
                        style.contains("line-through"),
                        style.contains("line-under")
                );
                fonts.add(font);
            }

            return fonts;
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
                font.getSizeInPoints()
        );
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
     * Convert {@link AffineTransformation2d} to JavaFX {@link Affine}.
     * @param at the affine transformation
     * @return the JavaFX affine transformation
     */
    public static Affine convert(AffineTransformation2d at) {
        return new Affine(
                at.getScaleX(), at.getShearX(), at.getTranslateX(), 
                at.getShearY(), at.getScaleY(), at.getTranslateY()
        );
    }

    /**
     * Convert JavaFX {@link Affine} to {@link AffineTransformation2d}.
     * @param a the JavaFX affine transformation
     * @return the affine transformation
     */
    public static AffineTransformation2d convert(Affine a) {
        return new AffineTransformation2d(
                (float) a.getMxx(), (float) a.getMyx(), (float) a.getTx(),
                (float) a.getMyx(), (float) a.getMyy(), (float) a.getTy()
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
