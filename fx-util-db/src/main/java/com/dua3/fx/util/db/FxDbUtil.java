// Copyright 2019 Axel Howind
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.dua3.fx.util.db;

import java.sql.Clob;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dua3.fx.util.PlatformHelper;
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
	private static final String ERROR_TEXT = "###";

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
     * @return 
     *  the number of rows read
     * @throws SQLException
     *  if an error occurs while reading from the ResultSet.
     */
    public static int fill(TableView<ObservableList<Object>> tv, ResultSet rs) throws SQLException {
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
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);
        
        // read result metadata
        LOG.finer("reading result meta data ...");
        ResultSetMetaData meta = rs.getMetaData();
        int nColumns = meta.getColumnCount();
        for (int i = 1; i <= nColumns; i++) {
            final int idx = i - 1;

            String label = meta.getColumnLabel(i);
            String name = meta.getColumnName(i);
            JDBCType sqlType = JDBCType.valueOf(meta.getColumnType(i));
            int scale = meta.getScale(i);

            // define the formatting
            Function<Object, String> format;
            switch (sqlType) {
            case DATE:
            	format = item -> DbUtil.toLocalDate(item).format(dateFormatter);
            	break;
            case TIMESTAMP:
            	format = item -> DbUtil.toLocalDateTime(item).format(timestampFormatter);
                break;
            case TIME:
                format = item ->  DbUtil.toLocalDateTime(item).format(timeFormatter);
                break;

            // numbers that have scale
            case DECIMAL:
            case NUMERIC:
                if (scale > 0) {
                    format = item -> String.format(
                                locale, 
                                "%.0"+scale+"f", 
                                ((Number)item).doubleValue());
                } else {
                    format = String::valueOf;
                }
                break;
                
            // numbers that do not have scale
            case DOUBLE:
            case REAL:
            case FLOAT:
                format = String::valueOf;
                break;

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

        // read result
        LOG.finer("reading result data ...");
        while (rs.next()) {
            var list = FXCollections.observableArrayList();
            for (int i = 1; i <= nColumns; i++) {
                list.add(getObject(rs, i));
            }
            newItems.add(list);
        }
        LOG.finer(() -> "read "+newItems.size()+" rows of data");

        LOG.finer("setting rows ...");
        PlatformHelper.runAndWait(() -> {
        	columns.setAll(newColumns);
        	items.setAll(newItems);
        });
        
        return newItems.size();
    }

	private static Object getObject(ResultSet rs, int i) throws SQLException {
		Object obj = rs.getObject(i);
		
		if (obj instanceof Clob) {
			obj = toString((Clob) obj);
		}
		
		return obj;
	}

	private static String toString(Clob clob) {
		try {
			return clob.getSubString(1, (int) Math.min(Integer.MAX_VALUE, clob.length()));
		} catch (SQLException e) {
			LOG.log(Level.WARNING, "could no convert Clob to String", e);
			return ERROR_TEXT;
		}
	}
    
}