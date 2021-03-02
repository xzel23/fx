package com.dua3.fx.editor;

import com.dua3.fx.util.FxUtil;
import com.dua3.utility.text.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RichTextArea extends StyledTextArea {

    private final Font baseFont;
    private final Map<FontDef, javafx.scene.text.Font> fontMap = new HashMap<>();
    
    public RichTextArea(Font baseFont) {
        this.baseFont = Objects.requireNonNull(baseFont);
    }
    
    public void setText(RichText text) {
        text.lines().map(this::createParagraph).forEach(this::add);
    }

    private TextFlow createParagraph(RichText text) {
        TextFlow paragraph = new TextFlow();
        for (Run run: text) {
            Text t = new Text(run.toString());
            t.setFont(getFont(run.getFontDef()));
            paragraph.getChildren().add(t);
        }
        return paragraph;
    }

    private javafx.scene.text.Font getFont(FontDef fontDef) {
        return fontMap.computeIfAbsent(fontDef, fd -> FxUtil.convert(baseFont.deriveFont(fd)));
    }

}
