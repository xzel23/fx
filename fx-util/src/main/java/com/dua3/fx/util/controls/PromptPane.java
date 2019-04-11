package com.dua3.fx.util.controls;

import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.util.logging.Logger;

public class PromptPane extends InputDialogPane<String> {

    /** Logger */
    protected static final Logger LOG = Logger.getLogger(PromptPane.class.getSimpleName());

    private TextField text;

	@Override
	public String get() {
		return text.getText();
	}

	public PromptPane() {
		text = new TextField();
		text.textProperty().addListener( (v,o,n) -> updateValidState(n) );
		setContent(new StackPane(text));
	}

	@Override
	public void init() {
		text.requestFocus();
		updateValidState(text.getText());
	}
}
