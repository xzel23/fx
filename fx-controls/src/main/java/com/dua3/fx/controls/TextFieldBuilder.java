package com.dua3.fx.controls;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public final class TextFieldBuilder {

    private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d*|0");
    private static final UnaryOperator<TextFormatter.Change> INTEGER_FILTER = change -> INTEGER_PATTERN.matcher(change.getControlNewText()).matches() ? change : null;

    private static TextFormatter<Integer> getIntegerTextFormatter(UnaryOperator<TextFormatter.Change> integerFilter) {
        return new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter);
    }

    private static final Pattern SIGNED_INTEGER_PATTERN = Pattern.compile("-?([1-9]\\d*|0)?");
    private static final UnaryOperator<TextFormatter.Change> SIGNED_INTEGER_FILTER = change -> SIGNED_INTEGER_PATTERN.matcher(change.getControlNewText()).matches() ? change : null;

    private String text;
    private TextFieldType type = TextFieldType.TEXT;

    static TextFieldBuilder builder() {
        return new TextFieldBuilder();
    }

    public TextFieldBuilder text(String text) {
        this.text = text;
        return this;
    }

    public TextFieldBuilder type(TextFieldType type) {
        this.type = type;
        return this;
    }

    public TextField build() {
        TextField tf = new TextField();

        switch (type) {
            case INTEGER -> tf.setTextFormatter(getIntegerTextFormatter(INTEGER_FILTER));
            case SIGNED_INTEGER -> tf.setTextFormatter(getIntegerTextFormatter(SIGNED_INTEGER_FILTER));
            case TEXT -> {}
        }

        if (text != null) {
            tf.setText(text);
        }

        return tf;
    }

}
