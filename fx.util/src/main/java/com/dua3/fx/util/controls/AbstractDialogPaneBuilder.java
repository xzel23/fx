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
	
	public static interface ResultHandler<R> {
		boolean handleResult(ButtonType btn, R result);
	}
	
    final BiConsumer<D, String> headerSetter;
	final BiConsumer<D, String> textSetter;

	AbstractDialogPaneBuilder(
		Supplier<D> supplier,
		BiConsumer<D, String> headerSetter,
		BiConsumer<D, String> textSetter
	) {
		this.supplier = supplier;
		this.headerSetter=headerSetter;
		this.textSetter=textSetter;
	}

	private Supplier<D> supplier;
	private String header = null;
	private String text = null;

	private ResultHandler<R> resultHandler = (b,r) -> true;

	protected void setSupplier(Supplier<D> supplier) {
		this.supplier = Objects.requireNonNull(supplier);
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
		D dlg = supplier.get();

		applyIfNotNull(headerSetter, dlg, header);
		applyIfNotNull(textSetter, dlg, text);

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

	/**
	 * Set text.
	 * @param fmt
	 * 	the format String as defined by {@link java.util.Formatter}
	 * @param args
	 * 	the arguments passed to the formatter
	 * @return 
	 * 	{@code this}
	 */
	@SuppressWarnings("unchecked")
	public B text(String fmt, Object... args) {
		this.text = format(fmt, args);
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
	
}
