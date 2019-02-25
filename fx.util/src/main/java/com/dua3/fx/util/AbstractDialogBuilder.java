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

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/** 
 * Abstract base class for Dialog builders.
 * 
 * Provides a fluent interface to create Dialogs. 
 */
public abstract class AbstractDialogBuilder<T,B extends AbstractDialogBuilder<T, B>> 
extends AbstractDialogPaneBuilder<T, B>{
	
	private final BiConsumer<T, String> titleSetter;

	AbstractDialogBuilder(
		Supplier<T> supplier,
		BiConsumer<T, String> titleSetter,
		BiConsumer<T, String> headerSetter,
		BiConsumer<T, String> textSetter
	) {
		super(supplier, headerSetter, textSetter);
		this.titleSetter=titleSetter;
	}

	private String title = null;

	/**
	 * Create Dialog instance.
	 * @return Dialog instance
	 */
	public T build() {
		T dlg = super.build();

		applyIfNotNull(titleSetter, dlg, title);

		return dlg;
	}

	/**
	 * Set dialog title.
	 * @param fmt
	 * 	the format String as defined by {@link java.util.Formatter}
	 * @param args
	 * 	the arguments passed to the formatter
	 * @return 
	 * 	{@code this}
	 */
	@SuppressWarnings("unchecked")
	public B title(String fmt, Object... args) {
		this.title = format(fmt, args);
		return (B) this;
	}

}
