package com.dua3.fx.util.controls;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.dua3.fx.util.FxUtil;

import javafx.event.ActionEvent;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

public class InputPane extends InputDialogPane<Map<String,Object>> {

    /** Logger */
    protected static final Logger LOG = Logger.getLogger(InputPane.class.getSimpleName());

	private static final String MARKER_INITIAL = "";
	private static final String MARKER_ERROR = "\u26A0";
	private static final String MARKER_OK = "";
	
	private GridPane grid = new GridPane();

	/**
	 * Meta data for a single input field consisting of ID, label text, default value etc.
	 *
	 * @param <T>
	 *  the input's value type
	 */
	static class Meta<T> {
		final String id;
		final Class<T> cls;
		final Supplier<T> dflt;
		final InputControl<T> control;
		Label label = new Label();
		Label marker = new Label();

		Meta(String id, String label, Class<T> cls, Supplier<T> dflt, InputControl<T> control) {
			this.id = id;
			this.label.setText(label);
			this.cls = cls;
			this.dflt = dflt;
			this.control = control;
			
			Dimension2D dimMarker = new Dimension2D(0,0);
			dimMarker = FxUtil.growToFit(dimMarker, marker.getBoundsInLocal());
			marker.setMinSize(dimMarker.getWidth(), dimMarker.getHeight());
            this.marker.setText(MARKER_INITIAL);
		}
		
		void reset() {
			control.set(dflt.get());
		}
		
		Optional<String> validate() {
			return control.validate();
		}
	}

	private Collection<Meta<?>> data = null;
	private int columns = 1;

	@Override
	public Map<String, Object> get() {
		// Collecors.toMap() does not support null values!
		Map<String,Object> result = new HashMap<>();
		data.stream().forEach(e -> result.put(e.id, e.control.get()));
		return result;
	}
	
	public InputPane() {
	}

	private void addToGrid(Node child, int c, int r, Insets insets) {
		grid.add(child, c, r);
		GridPane.setMargin(child, insets);
	}

	void setContent(Collection<Meta<?>> data, int columns) {
		this.data = Objects.requireNonNull(data);
		this.columns = columns;

		// buttons
		getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		final Button okButton = (Button) lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, ae -> {
		    if (!validate()) {
		        ae.consume(); //not valid
		    }
		});		
	}

	@Override
	public void init() {
		grid.getChildren().clear();
		
		// create grid with input controls
		Insets insets = new Insets(2);
		Insets markerInsets = new Insets(0);
		int r = 0, c = 0;
		for (var entry : data) {
			// add label and control
			addToGrid(entry.label, 3 * c, r, insets);
			addToGrid(entry.control.node(), 3 * c + 1, r, insets);
			addToGrid(entry.marker, 3 * c + 2, r, markerInsets);

			entry.control.init();
			
			// move to next position in grid
			c = (c + 1) % columns;
			if (c == 0) {
				r++;
			}
		}

		setContent(grid);
	}

	private boolean validate() {
		// validate all input fields. validation succeeds if no validation returns an error message.
		// do not use allMatches() because it might not process all items
		return data.stream()
			.map(item -> validateAndMark(item))
			.filter(Optional::isPresent)
			.count() == 0;
	}

	private Optional<String> validateAndMark(Meta<?> item) {
		Optional<String> result = item.validate();
		boolean ok = result.isEmpty();
		if (ok) {
			item.marker.setText(MARKER_OK);
			item.marker.setTooltip(null);
		} else {
			item.marker.setText(MARKER_ERROR);
			item.marker.setTooltip(new Tooltip(result.get()));			
		}
		return result;
	}

}
