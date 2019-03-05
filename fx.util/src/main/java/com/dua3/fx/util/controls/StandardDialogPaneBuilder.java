package com.dua3.fx.util.controls;

import java.util.function.Supplier;

import javafx.scene.control.DialogPane;

/**
 * Abstract base for DialogPane builders.
 *
 * @param <D> the type of the dialog or pane to build
 * @param <B> the type of the builder 
 * @param <R> the result type
 */
public class StandardDialogPaneBuilder<D extends DialogPane, B extends StandardDialogPaneBuilder<D,B,R>,R>
		extends AbstractDialogPaneBuilder<D, B,R> {
	protected StandardDialogPaneBuilder(Supplier<D> supplier) {
		super(supplier, DialogPane::setHeaderText, DialogPane::setContentText);
	}
	
    String next = null;
	
	@SuppressWarnings("unchecked")
	public B next(String s) {
		this.next = s;
		return (B) this;
	}

}
