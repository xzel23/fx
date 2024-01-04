package com.dua3.fx.util;

import com.dua3.cabe.annotations.Nullable;
import com.dua3.utility.data.Color;
import com.dua3.utility.text.Font;
import com.dua3.utility.text.FontDef;

import java.util.Objects;

public class FxFontEmbedded extends Font {

    private final javafx.scene.text.Font fxFont;

    FxFontEmbedded(javafx.scene.text.Font fxFont, String family, float size, Color color, boolean bold, boolean italic, boolean underline, boolean strikeThrough) {
        super(family, size, color, bold, italic, underline, strikeThrough);
        this.fxFont = fxFont;
    }

    public javafx.scene.text.Font fxFont() {
        return fxFont;
    }

    @Override
    public Font deriveFont(final FontDef fd) {
        // use the same base font if only attributes that are not part of JavaFX Font attributes
        if (isNullOrEquals(fd.getSize(), getSizeInPoints())
                && isNullOrEquals(fd.getFamily(), getFamily())
                && isNullOrEquals(fd.getBold(), isBold())
                && isNullOrEquals(fd.getItalic(), isItalic())) {

            return new FxFontEmbedded(fxFont, getFamily(), getSizeInPoints(), getColor(), isBold(), isItalic(), isUnderline(), isStrikeThrough());
        }

        // otherwise, use the standard method
        return super.deriveFont(fd);
    }

    private static <T> boolean isNullOrEquals(@Nullable T a, @Nullable T b) {
        return a == null || b == null || a.equals(b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.fxFont);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final FxFontEmbedded that = (FxFontEmbedded) o;
        return Objects.equals(this.fxFont, that.fxFont);
    }

}
