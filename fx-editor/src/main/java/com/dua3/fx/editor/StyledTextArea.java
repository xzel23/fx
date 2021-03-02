package com.dua3.fx.editor;

import javafx.scene.layout.Region;
import com.dua3.utility.text.RichText;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class StyledTextArea extends Region {
    
    public StyledTextArea() {
    }

    protected void add(TextFlow paragraph) {
        getChildren().add(Objects.requireNonNull(paragraph));
    }

}
