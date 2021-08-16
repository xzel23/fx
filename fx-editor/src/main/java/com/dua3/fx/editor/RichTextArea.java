package com.dua3.fx.editor;

import com.dua3.fx.util.FxUtil;
import com.dua3.utility.data.Pair;
import com.dua3.utility.text.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.HashMap;
import java.util.Map;

public class RichTextArea extends StyledTextArea<RichTextArea.Paragraph> {

    public class Paragraph extends TextFlow {
        Paragraph(RichText text) {
            for (Run run: text) {
                Text t = new Text(run.toString());
                Pair<Font, javafx.scene.text.Font> f = getFont(run.getFontDef());
                t.setFont(f.second());
                t.setFill(FxUtil.convert(f.first().getColor()));
                t.setUnderline(f.first().isUnderline());
                t.setStrikethrough(f.first().isStrikeThrough());
                getChildren().add(t);
            }
        }
    }
    
    private final Font baseFont;
    private final Map<FontDef, Pair<Font,javafx.scene.text.Font>> fontMap = new HashMap<>();
    
    public RichTextArea() {
        this(new Font().deriveFont(FxUtil.toFontDef(javafx.scene.text.Font.getDefault())));
    }
    
    public RichTextArea(Font baseFont) {
        this.baseFont = baseFont;
    }
    
    public void setText(RichText text) {
        text.lines().map(Paragraph::new).forEach(this::add);
    }

    private Pair<Font, javafx.scene.text.Font> getFont(FontDef fontDef) {
        return fontMap.computeIfAbsent(fontDef, fd -> {
            Font font = baseFont.deriveFont(fd);
            javafx.scene.text.Font fxFont = FxUtil.convert(font);
            return Pair.of(font,fxFont);
        });
    }

}
