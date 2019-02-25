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

package com.dua3.fx.util;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/** 
 * Abstract base class for DialogPane builders.
 * 
 * Provides a fluent interface to create Dialog panes. 
 */
public abstract class AbstractDialogPaneBuilder<T, B extends AbstractDialogPaneBuilder<T, B>> {
	
    String next = null;
	
    final BiConsumer<T, String> headerSetter;
	final BiConsumer<T, String> textSetter;

	AbstractDialogPaneBuilder(
		Supplier<T> supplier,
		BiConsumer<T, String> headerSetter,
		BiConsumer<T, String> textSetter
	) {
		this.supplier = supplier;
		this.headerSetter=headerSetter;
		this.textSetter=textSetter;
	}

	private Supplier<T> supplier;
	private String header = null;
	private String text = null;

	protected void setSupplier(Supplier<T> supplier) {
		this.supplier = Objects.requireNonNull(supplier);
	}

	protected <C,D> void applyIfNotNull(BiConsumer<C,D> consumer, C a, D b) {
		if (a!=null && b!=null) {
			consumer.accept(a,b);
		}
	}
	
	protected String format(String fmt, Object... args) {
		return args.length==0 ? fmt : String.format(fmt, args);
	}

	/**
	 * Create Alert instance.
	 * @return Alert instance
	 */
	public T build() {
		T dlg = supplier.get();

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
		this.text = format(fmt, args);
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B next(String s) {
		this.next = s;
		return (B) this;
	}
	
}
