package com.dua3.fx.util;

import java.util.Optional;
import java.util.function.Supplier;

import javafx.scene.control.Dialog;

public class StandardDialogBuilder<T extends Dialog<R>, B extends StandardDialogBuilder<T,B,R>, R>
		extends AbstractDialogBuilder<T, B> {
	protected StandardDialogBuilder(Supplier<T> supplier) {
		super(supplier, Dialog::setTitle, Dialog::setHeaderText, Dialog::setContentText);
	}

	/**
	 * Build and show the dialog.
	 * 
	 * This is equivalent to calling build().showAndWait().
	 * 
	 * @return
	 *  Optinal containing the result as defined by the dialog
	 */
	public Optional<R> showAndWait() {
		return build().showAndWait();
	}
	
}
