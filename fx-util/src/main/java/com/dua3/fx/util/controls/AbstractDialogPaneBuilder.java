// Copyright 2019 Axel Howind
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.dua3.fx.util.controls;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javafx.scene.control.ButtonType;

/** 
 * Abstract base class for DialogPane builders.
 * 
 * Provides a fluent interface to create Dialog panes. 
 * 
 * @param <D> the type of the dialog or pane to build
 * @param <B> the type of the builder 
 * @param <R> the result type
 */
public abstract class AbstractDialogPaneBuilder<D, B extends AbstractDialogPaneBuilder<D, B, R>, R> {
	
	/**
	 * Dialog(Pane) result handler.
	 *
	 * @param <R> the result type
	 */
	public interface ResultHandler<R> {
		/**
		 * Handle result.
		 * 
		 * @param btn
		 *  the button that was pressed
		 *  
		 * @param result
		 *  the dialog/pane result as returned by the result converter
		 *  
		 * @return
		 *  true, if it's ok to proceed (the current page should be left)
		 *  false otherwise
		 */
		boolean handleResult(ButtonType btn, R result);
	}
	
    private final BiConsumer<D, String> headerSetter;

	AbstractDialogPaneBuilder(
		BiConsumer<D, String> headerSetter
	) {
		this.dialogSupplier = () -> { throw new IllegalStateException("call setDialogSupplier() first"); };
		this.headerSetter=headerSetter;
	}

	private Supplier<D> dialogSupplier;
	private String header = null;

	private ResultHandler<R> resultHandler = (b,r) -> true;

	protected final void setDialogSupplier(Supplier<D> dialogSupplier) {
		this.dialogSupplier = Objects.requireNonNull(dialogSupplier);
	}

	protected static <C,D> void applyIfNotNull(BiConsumer<C,D> consumer, C a, D b) {
		if (a!=null && b!=null) {
			consumer.accept(a,b);
		}
	}
	
	protected static String format(String fmt, Object... args) {
		return String.format(fmt, args);
	}

	/**
	 * Create Alert instance.
	 * @return Alert instance
	 */
	public D build() {
		D dlg = dialogSupplier.get();

		applyIfNotNull(headerSetter, dlg, header);

		return dlg;
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
		this.header = format(fmt, args);
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B resultHandler(ResultHandler<R> resultHandler) {
		this.resultHandler = Objects.requireNonNull(resultHandler);
		return (B) this;
	}

	public ResultHandler<R> getResultHandler() {
		return resultHandler;
	}

	private Predicate<R> validate = r -> true;

	@SuppressWarnings("unchecked")
	public B validate(Predicate<R> validate) {
		this.validate = Objects.requireNonNull(validate);
		return (B) this;
	}

	protected Predicate<R> getValidate() {
		return validate;
	}
}
