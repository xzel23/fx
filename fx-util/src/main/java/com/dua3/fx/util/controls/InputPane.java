package com.dua3.fx.util.controls;

import javafx.scene.Node;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

public class InputPane extends InputDialogPane<Map<String,Object>> {

    /** Logger */
    protected static final Logger LOG = Logger.getLogger(InputPane.class.getName());

	private final InputGrid inputGrid;

	@Override
	public Map<String, Object> get() {
		Node content = getContent();
		if (content instanceof InputGrid) {
			return ((InputGrid) content).get();
		} else {
			return Collections.emptyMap();
		}
	}

	public InputPane(InputGrid inputGrid) {
		this.inputGrid = inputGrid;
		setContent(inputGrid);
	}

	@Override
	public void init() {
		inputGrid.init();
	}
}
