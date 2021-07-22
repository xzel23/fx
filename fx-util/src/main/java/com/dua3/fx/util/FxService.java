package com.dua3.fx.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javafx.concurrent.Service;

public abstract class FxService<T> extends Service<T> {

	protected FxService() {
	}

	@Override
	protected final FxTask<T> createTask() {
		FxTask<T> task = doCreateTask();
		task.progressProperty().addListener((v,o,n) -> updateTaskProgress(task, n.doubleValue()));
		task.stateProperty().addListener((v,o,n) -> updateTaskState(task, n));
		task.textProperty().addListener((v,o,n) -> updateTaskText(task, n));
		return task;
	}

	private void updateTaskText(FxTask<T> task, String arg) {
		taskTrackers.forEach(t -> t.updateTaskText(task, arg));
	}

	private void updateTaskState(FxTask<T> task, State arg) {
		taskTrackers.forEach(t -> t.updateTaskState(task, arg));
	}

	private void updateTaskProgress(FxTask<T> task, double arg) {
		taskTrackers.forEach(t -> t.updateTaskProgress(task, arg));
	}

	protected abstract FxTask<T> doCreateTask();

	private final List<FxTaskTracker> taskTrackers = new LinkedList<>();

	public void addTaskTracker(FxTaskTracker t) {
		taskTrackers.add(Objects.requireNonNull(t));
	}

	public void removeTaskTracker(FxTaskTracker t) {
		taskTrackers.remove(Objects.requireNonNull(t));
	}

}
