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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class InputDialog extends Dialog<Optional<Map<String, Object>>> {

	interface InputControl<T> {
		Control control();

		T get();

		void set(T arg);
	}

	static class Meta<T> {
		final String id;
		final String label;
		final Class<T> cls;
		final T dflt;
		final Supplier<InputControl<T>> createControl;
		T value;

		Meta(String id, String label, Class<T> cls, T dflt, Supplier<InputControl<T>> createControl) {
			this.id = id;
			this.label = label;
			this.cls = cls;
			this.dflt = dflt;
			this.value = dflt;
			this.createControl = createControl;
		}
	}

	private Collection<Meta<?>> data = null;

    public InputDialog() {
		setResultConverter(btn -> {
			if (btn!=ButtonType.OK) {
				return Optional.empty();
			}
			return Optional.of(
				data.stream().collect(Collectors.toMap(e -> e.id, e -> e.value))
			);
		});
     }

     private void addToGrid(GridPane grid, Control child, int c, int r, Insets insets) {
		grid.add(child, c, r);
		GridPane.setMargin(child, insets);
	}

	public void setContent(Collection<Meta<?>> data, int columns) {
		this.data = Objects.requireNonNull(data);
		
		// create grid with input controls
		GridPane grid = new GridPane();
		Insets insets = new Insets(2);
		int r = 0, c = 0;
		for (var entry : data) {
			// add label and control
			addToGrid(grid, new Label(entry.label), 2 * c, r, insets);
			addToGrid(grid, entry.createControl.get().control(), 2 * c + 1, r, insets);

			// move to next position in grid
			c = (c + 1) % columns;
			if (c == 0) {
				r++;
			}
		}

		DialogPane dialogPane = getDialogPane();
		
		dialogPane.setContent(grid);

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
	}

}
