package com.dua3.fx.util.controls;

import java.util.function.Supplier;

import javafx.scene.control.DialogPane;

public abstract class InputDialogPane<R> extends DialogPane implements Supplier<R> {

	public abstract void init();

}
