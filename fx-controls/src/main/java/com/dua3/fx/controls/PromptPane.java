package com.dua3.fx.controls;

import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PromptPane extends InputDialogPane<String> {

    /**
     * Logger
     */
    protected static final Logger LOG = LogManager.getLogger(PromptPane.class);

    private final TextField text;

    public PromptPane() {
        text = new TextField();
        text.textProperty().addListener((v, o, n) -> updateValidState(n));
        setContent(new StackPane(text));
    }

    @Override
    public String get() {
        return text.getText();
    }

    @Override
    public void init() {
        text.requestFocus();
        updateValidState(text.getText());
    }
}
