package com.dua3.fx.util.controls;

import java.util.function.Supplier;
import java.util.logging.Logger;

import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class PromptPane extends InputDialogPane<String> {

    /** Logger */
    protected static final Logger LOG = Logger.getLogger(PromptPane.class.getSimpleName());

    private TextField text;
    private Supplier<String> defaultValue = () -> "";

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
		updateValidState(text.getText());
	}
}
