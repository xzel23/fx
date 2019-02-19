package com.dua3.fx.util;

import javafx.concurrent.Worker.State;

public interface FxTaskTracker {
    void updateTaskState(FxTask<?> task, State state);
    void updateTaskProgress(FxTask<?> task, double progress);
    void updateTaskText(FxTask<?> task, String text);
}
