package com.dua3.fx.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public abstract class FxService<T> extends Service<T> {

	protected FxService() {
	}

	@Override
	protected final Task<T> createTask() {
		Task<T> task = doCreateTask();
		task.progressProperty().addListener((v,o,n) -> updateTaskProgress(task, n.doubleValue()));
		task.stateProperty().addListener((v,o,n) -> updateTaskState(task, n));
		task.titleProperty().addListener((v,o,n) -> updateTaskTitle(task, n));
		return task;
	}

	private void updateTaskTitle(Task<T> task, String arg) {
		taskTrackers.forEach(t -> t.updateTaskTitle(task, arg));
	}

	private void updateTaskMessage(Task<T> task, String arg) {
		taskTrackers.forEach(t -> t.updateTaskMessage(task, arg));
	}

	private void updateTaskState(Task<T> task, State arg) {
		taskTrackers.forEach(t -> t.updateTaskState(task, arg));
	}

	private void updateTaskProgress(Task<T> task, double arg) {
		taskTrackers.forEach(t -> t.updateTaskProgress(task, arg));
	}

	protected abstract Task<T> doCreateTask();

	private final List<FxTaskTracker> taskTrackers = new LinkedList<>();

	public void addTaskTracker(FxTaskTracker t) {
		taskTrackers.add(Objects.requireNonNull(t));
	}

	public void removeTaskTracker(FxTaskTracker t) {
		taskTrackers.remove(Objects.requireNonNull(t));
	}

}
