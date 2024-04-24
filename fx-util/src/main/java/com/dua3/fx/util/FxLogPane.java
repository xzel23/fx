package com.dua3.fx.util;

import com.dua3.cabe.annotations.Nullable;
import com.dua3.utility.data.Color;
import com.dua3.utility.logging.LogBuffer;
import com.dua3.utility.logging.LogEntry;
import com.dua3.utility.logging.LogLevel;
import com.dua3.utility.logging.LogUtil;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class FxLogPane extends BorderPane {

    public static final double COLUMN_WIDTH_MAX = Double.MAX_VALUE;
    public static final double COLUMN_WIDTH_LARGE = 10000.0;
    private final Function<LogEntry, Color> colorize;
    private final ToolBar toolBar;
    private final TextArea details;
    private final TableView<LogEntryBean> tableView;

    private volatile LogEntryBean selectedItem;

    private boolean autoScroll = true;

    private <T> TableColumn<LogEntryBean, T> createColumn(String name, String propertyName, boolean fixedWidth, String... sampleTexts) {
        TableColumn<LogEntryBean, T> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        if (sampleTexts.length == 0) {
            column.setPrefWidth(COLUMN_WIDTH_LARGE);
            column.setMaxWidth(COLUMN_WIDTH_MAX);
        }else {
            double w = 8 + Stream.of(sampleTexts).mapToDouble(s -> new Text(s).getLayoutBounds().getWidth()).max().orElse(80);
            column.setPrefWidth(w);
            if (fixedWidth) {
                column.setMinWidth(w);
                column.setMaxWidth(w);
            } else {
                column.setMaxWidth(COLUMN_WIDTH_MAX);
            }
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

        entries.addListener(this::onEntries);

        // add log level filtering to toolbar
        ComboBox<LogLevel> cbLogLevel = new ComboBox<>(FXCollections.observableArrayList(LogLevel.values()));
        cbLogLevel.valueProperty().addListener((v,o,n) -> entries.setPredicate(entry -> n.ordinal() <= entry.getLevel().ordinal()));
        cbLogLevel.setValue(LogLevel.INFO);

        toolBar.getItems().setAll(
                new Label("Level:"),
                cbLogLevel
        );

        // define table columns
        tableView.setEditable(false);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
        tableView.getColumns().setAll(
                createColumn("Time", "time", true, "8888-88-88T88:88:88.8888888"),
                createColumn("Level", "level", true, Arrays.stream(LogLevel.values()).map(Object::toString).toArray(String[]::new)),
                createColumn("Logger", "loggerName", false, "X".repeat(20)),
                createColumn("Message", "message", false, "X".repeat(60))
        );

        // disable autoscroll if the selection is not empty, enable when selection is cleared while scrolled to bottom
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                details.clear();
                autoScroll = autoScroll || isScrolledToBottom();
            } else {
                this.selectedItem = newSelection;
                autoScroll = false;
                details.setText(newSelection.getLogEntry().toString());
            }
        });

        //  ESC clears the selection
        tableView.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                clearSelection();
                autoScroll = true;
                event.consume();
            }
        });

        // automatically enable/disable autoscroll when user scrolls
        tableView.addEventFilter(ScrollEvent.ANY, this::onScrollEvent);

        SplitPane splitPane = new SplitPane(tableView, details);
        splitPane.setOrientation(Orientation.VERTICAL);

        setTop(toolBar);
        setCenter(splitPane);
    }

    private void onScrollEvent(ScrollEvent evt) {
        if (autoScroll) {
            // disable autoscroll when manually scrolling
            autoScroll = false;
        } else {
            // enable autoscroll when scrolling ends at end of input and selection is empty
            autoScroll = isSelectionEmpty() && isScrolledToBottom();
        }
    }

    private Optional<ScrollBar> getScrollBar(Orientation orientation) {
        for (Node node : tableView.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar sb && sb.getOrientation() == orientation) {
                return Optional.of(sb);
            }
        }
        return Optional.empty();
    }

    private boolean isSelectionEmpty() {
        return selectedItem == null;
    }

    private void clearSelection() {
        tableView.getSelectionModel().clearSelection();
        selectedItem = null;
    }

    private void onEntries(ListChangeListener.Change<? extends LogEntryBean> c) {
        if (autoScroll) {
            PlatformHelper.runLater(this::autoScrollToBottom);
        }
        if (!isSelectionEmpty()) {
            PlatformHelper.runLater(() -> tableView.getSelectionModel().select(selectedItem));
        }
    }

    private void autoScrollToBottom() {
        tableView.scrollTo(tableView.getItems().size()-1);
        autoScroll = true;
    }

    private boolean isScrolledToBottom() {
        return getScrollBar(Orientation.VERTICAL).map(sb -> {
                    double max = sb.getMax();
                    double current = sb.getValue();
                    double step = max / (1.0 + tableView.getItems().size());
                    return current >= max - step;
                })
                .orElse(true);
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
