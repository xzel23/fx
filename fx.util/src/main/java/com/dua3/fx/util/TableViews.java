package com.dua3.fx.util;

import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.scene.control.TableView;

public class TableViews {

    /** Logger */
    protected static final Logger LOG = Logger.getLogger(TableViews.class.getSimpleName());

    // utility - no instances
	private TableViews() {}
    
    /**
     * Clear TableView.
     * 
     * Clears both items and columns of the TableView instance.
     * 
     * @param tv the TableView
     */
    public static <T> void clear(TableView<T> tv) {
        Platform.runLater(() -> {
        	tv.getItems().clear();
            tv.getColumns().clear();
        });
    }

}
