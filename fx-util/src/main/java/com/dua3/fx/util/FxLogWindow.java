package com.dua3.fx.util;

import com.dua3.utility.logging.LogBuffer;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The FxLogWindow class represents a JavaFX window that displays log entries in a table view.
 * It extends the Stage class.
 */
public class FxLogWindow extends Stage {

    private final LogBuffer logBuffer;
    private final FxLogPane logPane;

    /**
     * Create a new FxLogWindow instance with a new {@link LogBuffer} using the default capacity;
     */
    public FxLogWindow() {
        this(new LogBuffer());
    }

    /**
     * Constructs a new instance of {@code FxLogWindow} with the specified maximum number of lines.
     *
     * @param maxLines the maximum number of lines to display in the log window
     */
    public FxLogWindow(int maxLines) {
        this(new LogBuffer(maxLines));
    }

    /**
     * Constructs a new instance of {@code FxLogWindow} using the provided {@link LogBuffer}.
     *
     * @param logBuffer the LogBuffer to use
     */
    public FxLogWindow(LogBuffer logBuffer) {
        this.logBuffer = logBuffer;
        logPane = new FxLogPane(this.logBuffer);
        Scene scene = new Scene(logPane, 800, 400);
        setScene(scene);
        setTitle("Log");
    }
}
