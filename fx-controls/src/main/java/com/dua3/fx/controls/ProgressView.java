package com.dua3.fx.controls;

import com.dua3.utility.concurrent.ProgressTracker;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;

import javax.swing.*;

public class ProgressView<T> extends GridPane implements ProgressTracker<T> {

    private final com.dua3.utility.concurrent.ProgressView<T> imp;

    private static class FxProgressBarIndicator implements com.dua3.utility.concurrent.ProgressView.ProgressIndicator {

        private final ProgressBar pb;

        FxProgressBarIndicator() {
            this.pb = new ProgressBar();
        }

        @Override
        public void finish(State s) {
            Platform.runLater(() -> pb.setProgress(1));
        }

        @Override
        public void update(int done, int total) {
            Platform.runLater(() -> {
                if (total == 0) {
                    pb.setProgress(-1);
                } else {
                    pb.setProgress((double) done / total);
                }
            });
        }

        /**
         * The integer value that corresponds to 100% in percentage mode. 
         */
        private static final int MAX = 10000;

        @Override
        public void update(double percentDone) {
            Platform.runLater(() -> pb.setProgress(percentDone));
        }
    }
    
    public ProgressView() {
        setHgap(8);
        
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.SOMETIMES);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.ALWAYS);
        getColumnConstraints().addAll(column1, column2); // first column gets any extra width

        this.imp = new com.dua3.utility.concurrent.ProgressView<>(this::createProgressIndicator);
    }

    private <T> com.dua3.utility.concurrent.ProgressView.ProgressIndicator createProgressIndicator(T t) {
        FxProgressBarIndicator pi = new FxProgressBarIndicator();
        
        int row = getChildren().size();
        Label label = new Label(t.toString());
        GridPane.setConstraints(label, 0, row);
        ProgressBar pb = pi.pb;
        pb.setMaxWidth(Double.POSITIVE_INFINITY);
        GridPane.setConstraints(pb, 1, row);
        getChildren().addAll(label, pb);
        
        return pi;
    }

    @Override
    public void schedule(T task) {
        imp.schedule(task);
    }

    @Override
    public void start(T task) {
        imp.update(task, 0, 0);
    }

    @Override
    public void pause(T task) {
        imp.pause(task);
    }

    @Override
    public void abort(T task) {
        imp.abort(task);
    }

    @Override
    public void finish(T task, State s) {
        imp.finish(task, s);
    }

    @Override
    public void update(T task, int total, int done) {
        imp.update(task, total, done);
    }

    @Override
    public void update(T task, double percentDone) {
        imp.update(task, percentDone);
    }

}
