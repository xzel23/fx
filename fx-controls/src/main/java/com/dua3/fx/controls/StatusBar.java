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

import com.dua3.fx.util.FxTaskTracker;
import com.dua3.fx.util.PlatformHelper;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Dialog to configure a editor settings.
 */
public class StatusBar extends HBox implements FxTaskTracker {

    /**
     * Logger instance
     */
    private static final Logger LOG = LoggerFactory.getLogger(StatusBar.class);

    // -- input controls
    @FXML
    Label text;
    @FXML
    ProgressBar progress;

    /**
     * Construct new StatusBar instance.
     */
    public StatusBar() {
        // load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("status_bar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            LOG.warn("exception while loading FXML", e);
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
    public void updateTaskState(Task<?> task, State state) {
        PlatformHelper.runLater(() -> {
            switch (state) {
                case RUNNING -> {
                    progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                    progress.setVisible(true);
                }
                case SUCCEEDED -> {
                    progress.setProgress(1.0);
                    progress.setVisible(false);
                }
                case READY -> {
                    progress.setProgress(0.0);
                    progress.setVisible(false);
                }
                case SCHEDULED -> {
                    progress.setProgress(0.0);
                    progress.setVisible(true);
                }
                case CANCELLED, FAILED -> {
                    progress.setProgress(0.0);
                    progress.setVisible(false);
                }
                default -> LOG.warn("StatusBar.updateTaskState() - unexpected state: {}", state);
            }
        });
    }

    @Override
    public void updateTaskProgress(Task<?> task, double p) {
        progress.setProgress(p);

    }

    @Override
    public void updateTaskTitle(Task<?> task, String s) {
        text.setText(s);
    }

    @Override
    public void updateTaskMessage(Task<?> task, String s) {
        progress.setTooltip(new Tooltip(s));
    }

}
