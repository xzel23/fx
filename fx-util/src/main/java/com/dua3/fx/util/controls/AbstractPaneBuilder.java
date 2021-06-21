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
public class AbstractPaneBuilder<D extends DialogPane & Supplier<R>, B extends AbstractPaneBuilder<D,B,R>,R>
		extends AbstractDialogPaneBuilder<D, B,R> {
	protected AbstractPaneBuilder() {
		super(DialogPane::setHeaderText);
	}
	
    protected String next;
	
	@SuppressWarnings("unchecked")
	public B next(String s) {
		this.next = s;
		return (B) this;
	}

}
