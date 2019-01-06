package com.dua3.fx.util.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dua3.utility.db.DbUtil;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class FxDbUtil {

    /** Logger */
    protected static final Logger LOG = Logger.getLogger(FxDbUtil.class.getSimpleName());

    // utility - no instances
	private FxDbUtil() {}
    
    /**
     * Fill TableView instance with data from {@link ResultSet}.
     * 
     * All columns will be removed and recreated based on the ResultSet's metadata.
     * @param tv
     *  the TableView
     * @param rs
     *  the ResultSet
     * @throws SQLException
     *  if an error occurs while reading from the ResultSet.
     */
    public static void fill(TableView<ObservableList<Object>> tv, ResultSet rs) throws SQLException {
        LOG.fine("populating TableView with ResultSet data");
        ObservableList<TableColumn<ObservableList<Object>, ?>> columns = tv.getColumns();
        var items = tv.getItems();

        LOG.finer("clearing tableview contents ...");
        Platform.runLater(items::clear);

        List<TableColumn<ObservableList<Object>, ?>> newColumns = new LinkedList<>();
        List<ObservableList<Object>> newItems = new LinkedList<>();

        Locale locale = Locale.getDefault();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
        DateTimeFormatter timestampFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        
        // read result metadata
        LOG.finer("reading result meta data ...");
        ResultSetMetaData meta = rs.getMetaData();
        int nColumns = meta.getColumnCount();
        for (int i = 1; i <= nColumns; i++) {
            final int idx = i - 1;

            String label = meta.getColumnLabel(i);
            String name = meta.getColumnName(i);
            int sqlType = meta.getColumnType(i);
            int scale = meta.getScale(i);

            // define the formatting
            Function<Object, String> format;
            switch (sqlType) {
            case Types.DATE:
            	format = item -> DbUtil.toLocalDate(item).format(dateFormatter);
            	break;
            case Types.TIMESTAMP:
            	format = item -> DbUtil.toLocalDateTime(item).format(timestampFormatter);
            	break;
            case Types.DOUBLE:
            case Types.REAL:
            case Types.FLOAT:
            case Types.DECIMAL:
            case Types.NUMERIC:
            	format = item -> String.format(
            				locale, 
            				"%.0"+scale+"f", 
            				((Number)item).doubleValue());
            	break;
            case Types.TIME: // TODO (fallthrough)
            default:
            	format = String::valueOf;
            	break;
            }
            LOG.log(Level.FINER, () -> String.format("column name: %s label: %s type: %s scale: %d", name, label, sqlType, scale));
            
            // CellValueFactory
            Callback<CellDataFeatures<ObservableList<Object>, Object>, ObservableValue<Object>> cellValueFactory
            	= param -> {
	                var list = param.getValue();
	                var x = idx < list.size() ? list.get(idx) : null;
	                return new ReadOnlyObjectWrapper<Object>(x);
	            };

	        // CellFactory
	        Callback<TableColumn<ObservableList<Object>, Object>, TableCell<ObservableList<Object>, Object>> cellFactory
				= col -> new TableCell<ObservableList<Object>, Object>() {				
				    @Override
				    protected void updateItem(Object item, boolean empty) {
						super.updateItem(item,empty);
						
						if (empty || item==null) {
							setText(null);
							setGraphic(null);
						} else {							
							setText(format.apply(item));
						}
				    }
				};

	        // create column
            TableColumn<ObservableList<Object>, Object> column = new TableColumn<>(label);
			column.setCellValueFactory(cellValueFactory);
			column.setCellFactory(cellFactory);
            newColumns.add(column);
        }
        LOG.finer("defining columns ...");
        Platform.runLater(() -> columns.setAll(newColumns));

        // read result
        LOG.finer("reading result data ...");
        while (rs.next()) {
            var list = FXCollections.observableArrayList();
            for (int i = 1; i <= nColumns; i++) {
                list.add(rs.getObject(i));
            }
            newItems.add(list);
        }
        LOG.finer(() -> "read "+newItems.size()+" rows of data");

        LOG.finer("setting rows ...");
        Platform.runLater(() -> items.setAll(newItems));
    }
    
}
