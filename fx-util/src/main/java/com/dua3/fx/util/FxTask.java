package com.dua3.fx.util;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

public abstract class FxTask<V> extends Task<V>{

	private static final AtomicInteger TASK_COUNTER = new AtomicInteger(0);
	final int taskId = TASK_COUNTER.incrementAndGet();

    private final StringProperty text = new SimpleStringProperty(this, "text", "");
    public final ReadOnlyStringProperty textProperty() { PlatformHelper.checkThread(); return text; }

    /* Use updateProgress() to set this task's progress. */

    /** 
	 * Set this task's status text. 
	 * 
	 * @param s
	 *  the status text to set
	 */
    protected void setText(String s) {
    	if (Platform.isFxApplicationThread()) {
        	text.set(s); 
    	} else {
    		Platform.runLater(() -> text.set(s));
    	}
    }
   
}
