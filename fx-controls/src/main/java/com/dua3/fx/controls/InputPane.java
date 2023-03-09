package com.dua3.fx.controls;

import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;


public class InputPane extends InputDialogPane<Map<String, Object>> {

    /**
     * Logger
     */
    protected static final Logger LOG = LoggerFactory.getLogger(InputPane.class);

    private final InputGrid inputGrid;

    public InputPane(InputGrid inputGrid) {
        this.inputGrid = inputGrid;
        setContent(inputGrid);
    }

    @Override
    public Map<String, Object> get() {
        Node content = getContent();
        if (content instanceof InputGrid) {
            return ((InputGrid) content).get();
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public void init() {
        inputGrid.init();
    }
}
