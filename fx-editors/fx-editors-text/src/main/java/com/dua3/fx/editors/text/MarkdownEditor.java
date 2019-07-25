package com.dua3.fx.editors.text;

import java.net.URL;

public class MarkdownEditor extends TextEditor {

    public static final URL FXML_URL = MarkdownEditor.class.getResource("editor_markdown.fxml");
    public static final URL HTML_URL = MarkdownEditor.class.getResource("node/editor_markdown.html");
    public static final String JS_EDITOR_INSTANCE = "markdown_editor";

    public MarkdownEditor() {
        super(FXML_URL, HTML_URL, JS_EDITOR_INSTANCE);
    }

}
