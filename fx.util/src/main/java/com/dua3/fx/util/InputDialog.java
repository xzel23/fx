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

package com.dua3.fx.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * A Dialog for inputting values.
 *
 * The dialog consists of labels and input controls laid out in a grid.
 */
public class InputDialog extends Dialog<Map<String, Object>> {

	private static final String MARKER_INITIAL = "";
	private static final String MARKER_ERROR = "\u26A0";
	private static final String MARKER_OK = "";
	
	/**
	 * Interface for an input field.
	 *
	 * @param <T> the input value's type
	 */
	public interface InputControl<T> {
		/**
		 * Create JavaFX control.
		 * 
		 * @return new Control instance
		 */
		Control control();

		/**
		 * Get value.
		 * 
		 * @return the current value
		 */
		T get();

		/**
		 * Set value.
		 * 
		 * @param arg the value to set
		 */
		void set(T arg);

		/**
		 * Validate user input.
		 * 
		 * @return if not valid, an Optional containing the error; otherwise an empty
		 *         Optional
		 */
		default Optional<String> validate() {
			return Optional.empty();
		}
	}

	/**
	 * Meta data for a single input field consisting of ID, label text, default value etc.
	 *
	 * @param <T>
	 *  the input's value type
	 */
	static class Meta<T> {
		final String id;
		final Class<T> cls;
		final T dflt;
		final InputControl<T> control;
		Label label = new Label();
		Label marker = new Label();

		Meta(String id, String label, Class<T> cls, T dflt, InputControl<T> control) {
			this.id = id;
			this.label.setText(label);
			this.marker.setText(MARKER_INITIAL);
			this.cls = cls;
			this.dflt = dflt;
			this.control = control;
		}
		
		void reset() {
			control.set(dflt);
		}
		
		Optional<String> validate() {
			return control.validate();
		}
	}

	private Collection<Meta<?>> data = null;

	public InputDialog() {
		setResultConverter(btn -> {
			if (btn != ButtonType.OK) {
				return null;
			}
			
			// Collecors.toMap() does not support null values!
			Map<String,Object> result = new HashMap<>();
			data.stream().forEach(e -> result.put(e.id, e.control.get()));
			
			return result;
		});
	}

	private void addToGrid(GridPane grid, Control child, int c, int r, Insets insets) {
		grid.add(child, c, r);
		GridPane.setMargin(child, insets);
	}

	void setContent(Collection<Meta<?>> data, int columns) {
		this.data = Objects.requireNonNull(data);

		// create grid with input controls
		GridPane grid = new GridPane();
		Insets insets = new Insets(2);
		Insets markerInsets = new Insets(0);
		int r = 0, c = 0;
		for (var entry : data) {
			// add label and control
			addToGrid(grid, entry.label, 3 * c, r, insets);
			addToGrid(grid, entry.control.control(), 3 * c + 1, r, insets);
			addToGrid(grid, entry.marker, 3 * c + 2, r, markerInsets);

			// move to next position in grid
			c = (c + 1) % columns;
			if (c == 0) {
				r++;
			}
		}

		DialogPane dialogPane = getDialogPane();
		dialogPane.setContent(grid);

		// buttons
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		final Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, ae -> {
		    if (!validate()) {
		        ae.consume(); //not valid
		    }
		});
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
		item.marker.setText(ok ? MARKER_OK : MARKER_ERROR);
		return result;
	}

}
