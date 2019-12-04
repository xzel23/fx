package com.dua3.fx.editors.text;

import java.net.URL;
import java.util.logging.Logger;

public class MarkdownEditor extends TextEditor {

    /**
     * Logger
     */
    private static final Logger LOG = Logger.getLogger(MarkdownEditor.class.getName());

    public static final URL FXML_URL = MarkdownEditor.class.getResource("editor_markdown.fxml");
    public static final URL HTML_URL = MarkdownEditor.class.getResource("node/editor_markdown.html");
    public static final String JS_EDITOR_INSTANCE = "markdown_editor";

    public MarkdownEditor() {
        super(FXML_URL, HTML_URL, JS_EDITOR_INSTANCE);
    }

    public void setShowPreview(boolean flag) {
        LOG.fine(() -> String.format("setting preview visibility: %s", flag));
        getBridge().call("setShowPreview", flag);
        if (!flag && !isShowEditor()) {
            setShowEditor(true);
        }
    }

    public void setShowEditor(boolean flag) {
        LOG.fine(() -> String.format("setting editor visibility: %s", flag));
        getBridge().call("setShowEditor", flag);
        if (!flag && !isShowPreview()) {
            setShowPreview(true);
        }
    }

    public boolean isShowPreview() {
        return getBridge().call("isShowPreview").equals(true);
    }

    public boolean isShowEditor() {
        return getBridge().call("isShowEditor").equals(true);
    }
}
