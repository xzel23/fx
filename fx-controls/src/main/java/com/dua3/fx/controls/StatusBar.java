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

package com.dua3.fx.controls;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dua3.fx.util.PlatformHelper;
import com.dua3.fx.util.FxTask;
import com.dua3.fx.util.FxTaskTracker;

import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;

/**
 * Dialog to configure a editor settings.
 */
public class StatusBar extends HBox implements FxTaskTracker {

	/** Logger instance */
    private static final Logger LOG = Logger.getLogger(StatusBar.class.getName());

    // -- input controls
	@FXML Label text;
	@FXML ProgressBar progress;

	/**
	 * Construct new StatusBar instance.
	 */
	public StatusBar() {
		// load FXML
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("intern/status_bar.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			LOG.log(Level.WARNING, "exception while loading FXML", e);
			throw new UncheckedIOException(e);
		}
	}

	@FXML
	private void initialize() {
	}

	public void setText(String s) {
		PlatformHelper.runLater(() -> text.setText(s));
	}

	public void setProgress(double p) {
		PlatformHelper.runLater(() -> progress.setProgress(p));
	}

	@Override
	public void updateTaskState(FxTask<?> task, State state) {
		PlatformHelper.runLater(() -> {
			switch (state) {
			case RUNNING:
				progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
				progress.setVisible(true);
				break;
			case SUCCEEDED:
				progress.setProgress(1.0);
				progress.setVisible(false);
				break;
			case READY:
				progress.setProgress(0.0);
				progress.setVisible(false);
				break;
			case SCHEDULED:
				progress.setProgress(0.0);
				progress.setVisible(true);
				break;
			case CANCELLED:
			case FAILED:
				progress.setProgress(0.0);
				progress.setVisible(false);
				break;
			default:
				LOG.warning("StatusBar.updateTaskState() - unexpected state: "+state);
				break;
			}
		});
	}

	@Override
	public void updateTaskProgress(FxTask<?> task, double p) {
		progress.setProgress(p);
		
	}

	@Override
	public void updateTaskText(FxTask<?> task, String s) {
		text.setText(s);
	}

}
