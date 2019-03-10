// Copyright 2019 Axel Howind
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.dua3.fx.util.controls;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import javafx.scene.control.Dialog;

/**
 * Abstract base class for Dialog builders.
 * Provides a fluent interface to create Dialogs.
 */
public abstract class AbstractDialogBuilder<D extends Dialog<R>, B extends AbstractDialogBuilder<D, B,R>,R>
    extends AbstractDialogPaneBuilder<D, B, R> {

  private final BiConsumer<D,String> titleSetter;

  protected AbstractDialogBuilder() {
    super(Dialog::setHeaderText);
    this.titleSetter = Dialog::setTitle;
  }

  private String title = null;

  /**
   * Create Dialog instance.
   * 
   * @return Dialog instance
   */
  @Override
  public D build() {
    D dlg = super.build();

    applyIfNotNull(titleSetter, dlg, title);

    return dlg;
  }

  /**
   * Set dialog title.
   * 
   * @param fmt
   *             the format String as defined by {@link java.util.Formatter}
   * @param args
   *             the arguments passed to the formatter
   * @return
   *         {@code this}
   */
  @SuppressWarnings("unchecked")
  public B title(String fmt, Object... args) {
    this.title = format(fmt, args);
    return (B) this;
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
