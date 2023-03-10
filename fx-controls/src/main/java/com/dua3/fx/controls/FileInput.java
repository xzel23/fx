package com.dua3.fx.controls;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

public class FileInput extends HBox implements InputControl<Path> {

    private final ObjectProperty<Path> value = new SimpleObjectProperty<>();

    private final InputBuilder.FileDialogMode mode;
    private final FileChooser.ExtensionFilter[] filters;
    private final Supplier<Path> dflt;

    private final TextField tfFilename;
    private final Button button;

    private final StringProperty error = new SimpleStringProperty("");
    private final BooleanProperty valid = new SimpleBooleanProperty(true);

    public FileInput(InputBuilder.FileDialogMode mode, Supplier<Path> dflt, FileChooser.ExtensionFilter... filters) {
        this.mode = Objects.requireNonNull(mode);
        this.filters = Arrays.copyOf(Objects.requireNonNull(filters), filters.length);
        this.dflt = Objects.requireNonNull(dflt);

        this.tfFilename = new TextField();
        this.button = new Button("...");
        button.setOnAction(evt -> {

            Path initialDir = value.get();
            if (initialDir != null && !Files.isDirectory(initialDir)) {
                initialDir = initialDir.getParent();
            }
            if (initialDir == null) {
                initialDir = Paths.get(".");
            }

            if (this.mode == InputBuilder.FileDialogMode.OPEN) {
                Dialogs.chooseFile()
                        .initialDir(initialDir)
                        .filter(this.filters)
                        .showOpenDialog(null)
                        .ifPresent(value::setValue);
            } else {
                Dialogs.chooseFile()
                        .initialDir(initialDir)
                        .filter(this.filters)
                        .showSaveDialog(null)
                        .ifPresent(value::setValue);
            }
        });

        this.getChildren().setAll(tfFilename, button);

        StringBinding fileName = Bindings.createStringBinding(() -> Objects.toString(value.get(), ""), value);
        tfFilename.textProperty().bind(fileName);

        // error property
        StringExpression errorText = Bindings.createStringBinding(
                () -> {
                    Path file = value.get();
                    if (file == null) {
                        return "No file selected.";
                    }
                    if (mode == InputBuilder.FileDialogMode.OPEN && !Files.exists(file)) {
                        return "File does not exist: " + file;
                    }
                    return "";
                },
                value
        );

        error.bind(errorText);

        // valid property
        BooleanExpression isNotNull = value.isNotNull();
        if (mode == InputBuilder.FileDialogMode.OPEN) {
            BooleanBinding exists = Bindings.createBooleanBinding(() -> getPath() != null && Files.exists(getPath()), value);
            valid.bind(Bindings.and(isNotNull, exists));
        } else {
            valid.bind(value.isNotNull());
        }
    }

    private Path getPath() {
        return value.get();
    }

    @Override
    public Node node() {
        return this;
    }

    @Override
    public void reset() {
        value.setValue(dflt.get());
    }

    @Override
    public Property<Path> valueProperty() {
        return value;
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid;
    }

    @Override
    public ReadOnlyStringProperty errorProperty() {
        return error;
    }
}
