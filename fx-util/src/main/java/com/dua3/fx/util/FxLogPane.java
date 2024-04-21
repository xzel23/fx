package com.dua3.fx.util;

import com.dua3.cabe.annotations.Nullable;
import com.dua3.utility.data.Color;
import com.dua3.utility.logging.LogBuffer;
import com.dua3.utility.logging.LogEntry;
import com.dua3.utility.logging.LogLevel;
import com.dua3.utility.logging.LogUtil;
import com.dua3.utility.text.FontUtil;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Orientation;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public class FxLogPane extends BorderPane {

    private final Function<LogEntry, Color> colorize;
    private final ToolBar toolBar;
    private final TextArea details;
    private final TableView<LogEntryBean> tableView;

    private <T> TableColumn<LogEntryBean, T> createColumn(String name, String propertyName, String... sampleTexts) {
        TableColumn<LogEntryBean, T> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        FxFontUtil fu = (FxFontUtil) FontUtil.getInstance();
        if (sampleTexts.length == 0) {
            column.setMinWidth(10);
            column.setMaxWidth(Double.MAX_VALUE);
        }else {
            double w = sampleTexts.length == 0
                    ? Double.MAX_VALUE
                    : Stream.of(sampleTexts).mapToDouble(s -> new Text(s).getLayoutBounds().getWidth()).max().orElse(80);
            column.setPrefWidth(w + 8);
        }
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
        this.toolBar = new ToolBar();
        this.tableView = new TableView<>(entries);
        this.details = new TextArea();

        ComboBox<LogLevel> cbLogLevel = new ComboBox<>(FXCollections.observableArrayList(LogLevel.values()));
        cbLogLevel.setValue(LogLevel.INFO);
        cbLogLevel.valueProperty().addListener((v,o,n) -> entries.setPredicate(entry -> n.ordinal() <= entry.getLevel().ordinal()));

        toolBar.getItems().setAll(
                new Label("Level:"),
                cbLogLevel
        );

        tableView.setEditable(false);
        tableView.getColumns().setAll(
                createColumn("Time", "time", "8888-88-88T88:88:88.8888888"),
                createColumn("Level", "level", Arrays.stream(LogLevel.values()).map(Object::toString).toArray(String[]::new)),
                createColumn("Logger", "loggerName", "X".repeat(40)),
                createColumn("Message", "message", "X".repeat(80))
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

        setTop(toolBar);
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
