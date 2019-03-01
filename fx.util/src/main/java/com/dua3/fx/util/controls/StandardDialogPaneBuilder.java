package com.dua3.fx.util.controls;

import java.util.function.Supplier;

import javafx.scene.control.DialogPane;

public class StandardDialogPaneBuilder<T extends DialogPane, B extends StandardDialogPaneBuilder<T,B,R>, R>
		extends AbstractDialogPaneBuilder<T, B> {
	protected StandardDialogPaneBuilder(Supplier<T> supplier) {
		super(supplier, DialogPane::setHeaderText, DialogPane::setContentText);
	}
	
    String next = null;
	
	@SuppressWarnings("unchecked")
	public B next(String s) {
		this.next = s;
		return (B) this;
	}

}
