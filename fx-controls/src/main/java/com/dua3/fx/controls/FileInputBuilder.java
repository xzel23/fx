package com.dua3.fx.controls;

import javafx.beans.value.ObservableValue;
import javafx.stage.FileChooser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public final class FileInputBuilder {

    private final FileDialogMode mode;
    private final List<FileChooser.ExtensionFilter> extensionFilters = new ArrayList<>();
    private Supplier<Path> initialPath = () -> null;
    private boolean existingOnly = true;
    private ObservableValue<Boolean> disabled;

    FileInputBuilder(FileDialogMode mode) {
        this.mode = mode;
    }

    public FileInputBuilder disabled(ObservableValue<Boolean> disabled) {
        this.disabled = disabled;
        return this;
    }

    public FileInputBuilder intialPath(Path initialPath) {
        this.initialPath = () -> initialPath;
        return this;
    }

    public FileInputBuilder intialPath(Supplier<Path> initialPath) {
        this.initialPath = initialPath;
        return this;
    }

    public FileInputBuilder filter(FileChooser.ExtensionFilter... filter) {
        extensionFilters.addAll(Arrays.asList(filter));
        return this;
    }

    public FileInputBuilder existingOnly(boolean flag) {
        this.existingOnly = flag;
        return this;
    }

    public FileInput build() {
        FileInput control = new FileInput(mode, initialPath, extensionFilters);
        control.setExistingOnly(existingOnly);
        if (disabled != null) {
            control.disableProperty().bind(disabled);
        }

        return control;
    }

}
