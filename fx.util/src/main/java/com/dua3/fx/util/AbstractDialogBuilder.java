package com.dua3.fx.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javafx.scene.control.Dialog;

/** 
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts. 
 */
public abstract class AbstractDialogBuilder<R, T extends Dialog<R>, B extends AbstractDialogBuilder<R, T, B>> {
	AbstractDialogBuilder(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	private Supplier<T> supplier;
	private String title = null;
	private String header = null;
	private String text = null;

	protected void setSupplier(Supplier<T> supplier) {
		this.supplier = Objects.requireNonNull(supplier);
	}
	
	/**
	 * Create Alert instance.
	 * @return Alert instance
	 */
	public T build() {
		T dlg = supplier.get();

		if (title != null) {
			dlg.setTitle(title);
		}

		if (header != null) {
			dlg.setHeaderText(header);
		}

		if (text != null) {
			dlg.setContentText(text);
		}

		return dlg;
	}

	/**
	 * Set Alert title.
	 * @param fmt
	 * 	the format String as defined by {@link java.util.Formatter}
	 * @param args
	 * 	the arguments passed to the formatter
	 * @return 
	 * 	{@code this}
	 */
	@SuppressWarnings("unchecked")
	public B title(String fmt, Object... args) {
		this.title = String.format(fmt, args);
		return (B) this;
	}

	/**
	 * Set Alert header text.
	 * @param fmt
	 * 	the format String as defined by {@link java.util.Formatter}
	 * @param args
	 * 	the arguments passed to the formatter
	 * @return 
	 * 	{@code this}
	 */
	@SuppressWarnings("unchecked")
	public B header(String fmt, Object... args) {
		this.header = String.format(fmt, args);
		return (B) this;
	}

	/**
	 * Set Alert text.
	 * @param fmt
	 * 	the format String as defined by {@link java.util.Formatter}
	 * @param args
	 * 	the arguments passed to the formatter
	 * @return 
	 * 	{@code this}
	 */
	@SuppressWarnings("unchecked")
	public B text(String fmt, Object... args) {
		this.text = fmt != null ? String.format(fmt, args) : "";
		return (B) this;
	}

	/**
	 * Build and show the alert.
	 * 
	 * This is equivalent to calling build().showAndWait().
	 * 
	 * @return
	 *  Optinal containingg the button pressed as returned by Alert.showAndWait()
	 */
	public Optional<R> showAndWait() {
		return build().showAndWait();
	}
	
}
