package com.dua3.fx.util;

import com.dua3.cabe.annotations.Nullable;
import com.dua3.utility.data.Color;
import com.dua3.utility.logging.LogBuffer;
import com.dua3.utility.logging.LogEntry;
import com.dua3.utility.logging.LogUtil;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.util.function.Function;

public class FxLogPane extends BorderPane {

    private final Function<LogEntry, Color> colorize;
    private final TextArea details;
    private TableView<LogEntryBean> tableView;

    private <T> TableColumn<LogEntryBean, T> createColumn(String name, String propertyName) {
        TableColumn<LogEntryBean, T> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(@Nullable T item, boolean empty) {
                super.updateItem(item, empty);
                TableRow<LogEntryBean> row = getTableRow();
                if (empty || row == null || row.getItem() == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(item == null ? "" : item.toString());
                    Color textColor = colorize.apply(row.getItem().getLogEntry());
                    setTextFill(FxUtil.convert(textColor));
                }
                super.updateItem(item, empty);
            }
        });
        return column;
    }

    public FxLogPane() {
        this(LogBuffer.DEFAULT_CAPACITY);
    }

    public FxLogPane(int bufferSize) {
        this(createBuffer(bufferSize));
    }

    public FxLogPane(LogBuffer buffer) {
        this(buffer, FxLogPane::defaultColorize);
    }

    public FxLogPane(LogBuffer buffer, Function<LogEntry, Color> colorize) {
        FilteredList<LogEntryBean> entries = new FilteredList<>(new LogEntriesObservableList(buffer), p -> true);

        this.colorize = colorize;
        this.tableView = new TableView<>(entries);
        this.details = new TextArea();

        tableView.setEditable(false);
        tableView.getColumns().setAll(
                createColumn("Time", "time"),
                createColumn("Level", "level"),
                createColumn("Logger", "loggerName"),
                createColumn("Message", "message")
        );
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                details.clear();
            } else {
                details.setText(newSelection.getLogEntry().toString());
            }
        });

        SplitPane splitPane = new SplitPane(tableView, details);
        splitPane.setOrientation(Orientation.VERTICAL);

        setCenter(splitPane);
    }

    /**
     * Creates a LogBuffer with the given buffer size and adds it to the global log entry handler.
     *
     * @param bufferSize the size of the buffer
     * @return the created LogBuffer
     */
    private static LogBuffer createBuffer(int bufferSize) {
        LogBuffer buffer = new LogBuffer(bufferSize);
        LogUtil.getGlobalDispatcher().addLogEntryHandler(buffer);
        return buffer;
    }

    /**
     * Default colorize method used in the FxLogPane class to determine the color of log entries.
     *
     * @param entry the log entry to be colorized
     * @return the Color object representing the color for the given log entry
     */
    private static Color defaultColorize(LogEntry entry) {
        return switch (entry.level()) {
            case ERROR -> Color.DARKRED;
            case WARN -> Color.RED;
            case INFO -> Color.DARKBLUE;
            case DEBUG -> Color.BLACK;
            case TRACE -> Color.DARKGRAY;
        };
    }


}
