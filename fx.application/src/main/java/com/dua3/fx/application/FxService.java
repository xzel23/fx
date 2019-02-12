package com.dua3.fx.application;

import java.util.Objects;

import javafx.concurrent.Service;

public abstract class FxService<A extends FxApplication<A, C>, C extends FxController<A, C>, T> extends Service<T> {

	private final C controller;
	
	public FxService(C controller) {
		this.controller = Objects.requireNonNull(controller);
	}

	public C getController() {
		return controller;
	}

	public void setStatus(String text) {
		getController().setStatusText(text);
	}
}
