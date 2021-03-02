package com.dua3.fx.editor;

import javafx.scene.Node;
import javafx.scene.text.Text;

import java.util.Collection;

public interface Paragraph<S extends Object, N extends Node> {
    Collection<N> nodes();
}
