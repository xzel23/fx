package com.dua3.fx.util;

import com.dua3.utility.data.DataUtil;
import com.dua3.utility.data.Image;
import com.dua3.utility.io.IoUtil;
import com.dua3.utility.math.geometry.AffineTransformation2f;
import com.dua3.utility.math.geometry.FillRule;
import com.dua3.utility.text.FontDef;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
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
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * JavaFX utility class.
 */
public final class FxUtil {

    private static final Pattern PATTERN_FILENAME_AND_DOT = Pattern.compile("^\\*\\.");

    private FxUtil() {}

    public static String asText(URI uri) {
        return uri == null ? "" : URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8);
    }

    /**
     * Convert JavaFX {@link Font} to {@link FontDef}.
     *
     * @param font the font
     * @return the FontDef
     */
    public static FontDef toFontDef(Font font) {
        FontDef fd = new FontDef();
        fd.setFamily(font.getFamily());
        fd.setSize((float) font.getSize());
        return fd;
    }

    /**
     * Convert {@link com.dua3.utility.data.Color} to {@link Color}.
     *
     * @param color the color
     * @return the JavaFX color
     */
    public static Color convert(com.dua3.utility.data.Color color) {
        int argb = color.argb();

        int a = (argb >> 24) & 0xff;
        int r = (argb >> 16) & 0xff;
        int g = (argb >> 8) & 0xff;
        int b = (argb) & 0xff;

        return Color.rgb(r, g, b, a / 255.0);
    }

    /**
     * Convert {@link Color to }{@link com.dua3.utility.data.Color}.
     *
     * @param color the JavaFX color
     * @return the color
     */
    public static com.dua3.utility.data.Color convert(Color color) {
        return com.dua3.utility.data.Color.rgb(
                (int) Math.round(color.getRed() * 255.0),
                (int) Math.round(color.getGreen() * 255.0),
                (int) Math.round(color.getBlue() * 255.0),
                (int) Math.round(color.getOpacity() * 255.0)
        );
    }

    /**
     * Convert {@link FillRule} to JavaFX {@link javafx.scene.shape.FillRule}.
     *
     * @param rule the fill rule
     * @return the JavaFX fill rule
     */
    public static javafx.scene.shape.FillRule convert(FillRule rule) {
        return rule == FillRule.EVEN_ODD ? javafx.scene.shape.FillRule.EVEN_ODD : javafx.scene.shape.FillRule.NON_ZERO;
    }

    /**
     * Convert {@link javafx.scene.shape.FillRule} to JavaFX {@link FillRule}.
     *
     * @param rule JavaFX the fill rule
     * @return the fill rule
     */
    public static FillRule convert(javafx.scene.shape.FillRule rule) {
        return rule == javafx.scene.shape.FillRule.EVEN_ODD ? FillRule.EVEN_ODD : FillRule.NON_ZERO;
    }

    /**
     * Convert {@link AffineTransformation2f} to JavaFX {@link Affine}.
     *
     * @param at the affine transformation
     * @return the JavaFX affine transformation
     */
    public static Affine convert(AffineTransformation2f at) {
        return new Affine(
                at.getScaleX(), at.getShearX(), at.getTranslateX(),
                at.getShearY(), at.getScaleY(), at.getTranslateY()
        );
    }

    /**
     * Convert JavaFX {@link Affine} to {@link AffineTransformation2f}.
     *
     * @param a the JavaFX affine transformation
     * @return the affine transformation
     */
    public static AffineTransformation2f convert(Affine a) {
        return new AffineTransformation2f(
                (float) a.getMxx(), (float) a.getMyx(), (float) a.getTx(),
                (float) a.getMyx(), (float) a.getMyy(), (float) a.getTy()
        );
    }

    public static Bounds getTextBounds(CharSequence s, com.dua3.utility.text.Font f) {
        return boundsInLocal(s, f);
    }

    private static Bounds boundsInLocal(CharSequence s, com.dua3.utility.text.Font f) {
        Text text = new Text(s.toString());
        text.setFont(convert(f));
        return text.getBoundsInLocal();
    }

    /**
     * Convert {@link com.dua3.utility.text.Font} to JavaFX {@link Font}.
     *
     * @param font the font
     * @return the JavaFX Font
     */
    public static Font convert(com.dua3.utility.text.Font font) {
        if (font instanceof FxFontEmbedded fxf) {
            return fxf.fxFont();
        }

        return Font.font(
                font.getFamily(),
                font.isBold() ? FontWeight.BOLD : FontWeight.NORMAL,
                font.isItalic() ? FontPosture.ITALIC : FontPosture.REGULAR,
                font.getSizeInPoints()
        );
    }

    public static double getTextWidth(CharSequence s, com.dua3.utility.text.Font f) {
        return boundsInLocal(s, f).getWidth();
    }

    public static double getTextHeight(CharSequence s, com.dua3.utility.text.Font f) {
        return boundsInLocal(s, f).getHeight();
    }

    public static Dimension2D growToFit(Dimension2D a, Bounds b) {
        return new Dimension2D(Math.max(a.getWidth(), b.getWidth()), Math.max(a.getHeight(), b.getHeight()));
    }

    /**
     * Test if file matches filter.
     *
     * @param filter the filter
     * @param file   the file
     * @return true if filename matches filter
     */
    public static boolean matches(FileChooser.ExtensionFilter filter, Path file) {
        return matches(filter, file.toString());
    }

    /**
     * Test if filename matches filter.
     *
     * @param filter   the filter
     * @param filename the filename
     * @return true if filename matches filter
     */
    public static boolean matches(FileChooser.ExtensionFilter filter, String filename) {
        String fext = IoUtil.getExtension(filename).toLowerCase(Locale.ROOT);
        return filter.getExtensions().stream()
                .map(ext -> PATTERN_FILENAME_AND_DOT.matcher(ext).replaceFirst("").toLowerCase(Locale.ROOT))
                .anyMatch(ext -> Objects.equals(ext, fext));
    }

    /**
     * Test if filename matches filter.
     *
     * @param filter the filter
     * @param file   the file
     * @return true if file matches filter
     */
    public static boolean matches(FileChooser.ExtensionFilter filter, File file) {
        return matches(filter, file.getName());
    }

    /**
     * Test if URI matches filter.
     *
     * @param filter the filter
     * @param uri    the URI
     * @return true if file matches filter
     */
    public static boolean matches(FileChooser.ExtensionFilter filter, URI uri) {
        return matches(filter, uri.getPath());
    }

    /**
     * Copy text to clipboard.
     *
     * @param s the text
     */
    public static void copyToClipboard(String s) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(s);
        clipboard.setContent(content);
    }

    /**
     * Copy image to clipboard.
     *
     * @param img the image
     */
    public static void copyToClipboard(Image img) {
        copyToClipboard(FxImageUtil.instance().convert(img));
    }

    /**
     * Copy image to clipboard.
     *
     * @param img the image
     */
    public static void copyToClipboard(javafx.scene.image.Image img) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putImage(img);
        clipboard.setContent(content);
    }

    /**
     * Copy file/folder to clipboard.
     *
     * @param path the path to the file/folder to copy to the clipboard
     */
    public static void copyToClipboard(Path path) {
        copyToClipboard(List.of(path));
    }

    /**
     * Copy files/folders to clipboard.
     *
     * @param paths the list of paths to copy to the clipboard
     */
    public static void copyToClipboard(Collection<Path> paths) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        List<File> files = paths.stream().map(Path::toAbsolutePath).map(Path::toFile).toList();
        content.putFiles(files);
        clipboard.setContent(content);
    }

    /**
     * Create new {@link MenuItem}.
     *
     * @param text    the text to show
     * @param graphic the graphic to show before the text
     * @param action  the action to perform when the menu item is invoked
     * @return new menu item
     */
    public static MenuItem createMenuItem(String text, Node graphic, Runnable action) {
        return createMenuItem(text, graphic, action, true);
    }

    /**
     * Create new {@link MenuItem}.
     *
     * @param text    the text to show
     * @param graphic the graphic to show before the text
     * @param enabled the enabled state
     * @param action  the action to perform when the menu item is invoked
     * @return new menu item
     */
    public static MenuItem createMenuItem(String text, Node graphic, Runnable action, boolean enabled) {
        MenuItem mi = new MenuItem(text, graphic);
        mi.setDisable(!enabled);
        mi.setOnAction(evt -> action.run());
        return mi;
    }

    /**
     * Create new {@link MenuItem}.
     *
     * @param text   the text to show
     * @param action the action to perform when the menu item is invoked
     * @return new menu item
     */
    public static MenuItem createMenuItem(String text, Runnable action) {
        return createMenuItem(text, action, true);
    }

    /**
     * Create new {@link MenuItem}.
     *
     * @param text    the text to show
     * @param enabled the enabled state
     * @param action  the action to perform when the menu item is invoked
     * @return new menu item
     */
    public static MenuItem createMenuItem(String text, Runnable action, boolean enabled) {
        MenuItem mi = new MenuItem(text);
        mi.setDisable(!enabled);
        mi.setOnAction(evt -> action.run());
        return mi;
    }

    /**
     * Create an {@link EventHandler<DragEvent>} that accepts dragging files.
     *
     * @param modeGetter Function that determines the supported {@link TransferMode}s.
     *                   Should return an empty list if the drag is not accepted.
     * @return event handler
     */
    public static EventHandler<DragEvent> dragEventHandler(Function<? super List<Path>, ? extends Collection<TransferMode>> modeGetter) {
        return event -> {
            Dragboard db = event.getDragboard();
            List<Path> files = DataUtil.convert(db.getFiles(), File::toPath);
            TransferMode[] modes = modeGetter.apply(files).toArray(TransferMode[]::new);
            event.acceptTransferModes(modes);
            event.consume();
        };
    }

    /**
     * Create an {@link EventHandler<DragEvent>} that accepts paths.
     *
     * @param processor consumer that processes the drop event
     * @return event handler
     */
    public static EventHandler<DragEvent> dropEventHandler(Consumer<? super List<Path>> processor) {
        return event -> {
            Dragboard db = event.getDragboard();
            List<Path> paths = DataUtil.convert(db.getFiles(), File::toPath);
            processor.accept(paths);
            event.setDropCompleted(true);
            event.consume();
        };
    }

    /**
     * Create union of two rectangles. The union here is defined as the rectangle r of minimum size that contains
     * both rectangles r1 and r2.
     *
     * @param r1 first rectangle
     * @param r2 second rectangle
     * @return minimal rectangle containing both r1 and r2
     */
    public static Rectangle2D union(Rectangle2D r1, Rectangle2D r2) {
        var xMin = Math.min(r1.getMinX(), r2.getMinX());
        var yMin = Math.min(r1.getMinY(), r2.getMinY());
        var xMax = Math.max(r1.getMaxX(), r2.getMaxX());
        var yMax = Math.max(r1.getMaxY(), r2.getMaxY());
        return new Rectangle2D(xMin, yMin, xMax - xMin, yMax - yMin);
    }
}
