package com.dua3.fx.util.controls;

import com.dua3.fx.util.FxUtil;
import javafx.event.ActionEvent;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;

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
