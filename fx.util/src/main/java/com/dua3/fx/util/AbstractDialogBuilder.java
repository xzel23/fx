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
		this.title = args.length==0 ? fmt : String.format(fmt, args);
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
		this.header = args.length==0 ? fmt : String.format(fmt, args);
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
		this.text = args.length==0 ? fmt : String.format(fmt, args);
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
