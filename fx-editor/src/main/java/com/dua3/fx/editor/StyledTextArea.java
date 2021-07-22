package com.dua3.fx.editor;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import java.util.Objects;

public abstract class StyledTextArea<P extends Node> extends Region {
    
    protected StyledTextArea() {
    }

    protected void add(P paragraph) {
        Objects.requireNonNull(paragraph);

        ObservableList<Node> children = getChildren();
        double y = children.isEmpty() ? 0 : children.get(children.size()-1).boundsInParentProperty().get().getMaxY();
        paragraph.setLayoutY(y);
        children.add(paragraph);
    }

}
