package com.dua3.fx.util.controls;

import com.dua3.fx.util.FxUtil;
import com.dua3.utility.data.Pair;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.value.ObservableBooleanValue;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

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
	}

	private Collection<Meta<?>> data = null;
	private int columns = 1;

	@Override
	public Map<String, Object> get() {
		// Collecors.toMap() does not support null values!
		Map<String,Object> result = new HashMap<>();
		data.forEach(e -> result.put(e.id, e.control.get()));
		return result;
	}
	
	public InputPane() {
	}

	private void addToGrid(Node child, int c, int r, int span, Insets insets) {
		grid.add(child, c, r, span, 1);
		GridPane.setMargin(child, insets);
	}

	void setContent(Collection<Meta<?>> data, int columns, List<Pair<ButtonType, Consumer<InputDialogPane>>> buttons) {
		this.data = Objects.requireNonNull(data);
		this.columns = columns;
		this.buttons = new ArrayList<>(buttons);
	}

	@Override
	public void init() {
		grid.getChildren().clear();

		List<BooleanExpression> validators = new ArrayList<>();

		// create grid with input controls
		Insets insets = new Insets(2);
		Insets markerInsets = new Insets(0);
		int r = 0, c = 0;
		for (var entry : data) {
			// add label and control
		    int gridX = 3*c;
		    int gridY = r;

		    int span;
		    if (entry.label != null) {
		        addToGrid(entry.label, gridX, gridY, 1, insets);
		        gridX++;
		        span = 1;
		    } else {
		        span = 2;
		    }

		    validators.add(entry.control.validProperty());
		    
			addToGrid(entry.control.node(), gridX, gridY, span, insets);
			gridX += span;
			
			addToGrid(entry.marker, gridX, gridY, 1, markerInsets);

			entry.control.init();
			
			// move to next position in grid
			c = (c + 1) % columns;
			if (c == 0) {
				r++;
			}
		}

		// valid state is true if all inputs are valid
		ObservableBooleanValue[] inputs = validators.toArray(ObservableBooleanValue[]::new);
		Callable<Boolean> check = () -> {
			for (var value: inputs) {
				if (!value.get()) {
					return Boolean.FALSE;
				}
			}
			return Boolean.TRUE;
		};
		BooleanBinding binding = Bindings.createBooleanBinding(check, inputs);
		valid.bind(binding);

		setContent(grid);

		for (var entry: data) {
			entry.control.node().requestFocus();
			break;
		}
	}

}
