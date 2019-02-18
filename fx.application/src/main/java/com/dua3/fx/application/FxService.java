package com.dua3.fx.application;

import javafx.concurrent.Service;

public abstract class FxService<T> extends Service<T> {

	public FxService() {
	}

	@Override
	protected abstract FxTask<T> createTask();

}
