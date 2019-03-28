package com.dua3.fx.util.controls;

import com.dua3.fx.util.Dialogs;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;

public class FileInput extends HBox implements InputControl<File> {

    private final ObjectProperty<File> value = new SimpleObjectProperty<>();

    private final InputBuilder.FileDialogMode mode;
    private final FileChooser.ExtensionFilter filter;
    private final Supplier<File> dflt;

    private final TextField tfFilename;
    private final Button button;

    private final StringProperty error = new SimpleStringProperty("");
    private final BooleanProperty valid = new SimpleBooleanProperty(true);

    public FileInput(InputBuilder.FileDialogMode mode, Supplier<File> dflt, FileChooser.ExtensionFilter filter) {
        this.mode = Objects.requireNonNull(mode);
        this.filter = Objects.requireNonNull(filter);
        this.dflt = Objects.requireNonNull(dflt);

        this.tfFilename = new TextField();
        this.button = new Button("...");
        button.setOnAction(evt -> {

            File initialDir = value.get();
            if (initialDir!=null && !initialDir.isDirectory()) {
                initialDir = initialDir.getParentFile();
            }
            if (initialDir==null) {
                initialDir=new File(".");
            }

            if (mode== InputBuilder.FileDialogMode.OPEN) {
                Dialogs.chooseFile()
                        .initialDir(initialDir)
                        .filter(filter)
                        .showOpenDialog(null)
                        .ifPresent(f -> value.setValue(f));
            } else {
                Dialogs.chooseFile()
                        .initialDir(initialDir)
                        .filter(filter)
                        .showSaveDialog(null)
                        .ifPresent(f -> value.setValue(f));
            }
        });

        this.getChildren().setAll(tfFilename, button);

        StringBinding fname = Bindings.createStringBinding(() -> Objects.toString(value.get(), ""), value);
        tfFilename.textProperty().bind(fname);

        // error property
        StringExpression errorText = Bindings.createStringBinding(
                () -> {
                    File file = value.get();
                    if (file == null ) {
                        return "No file selected.";
                    }
                    if (mode==InputBuilder.FileDialogMode.OPEN && !Files.exists(file.toPath())) {
                        return "File does not exist: "+file;
                    }
                    return "";
                },
                value
        );

        error.bind(errorText);

        // valid property
        BooleanExpression isNotNull = value.isNotNull();
        if (mode== InputBuilder.FileDialogMode.OPEN) {
            BooleanBinding exists = Bindings.createBooleanBinding(() -> getPath()!=null && Files.exists(getPath()), value);
            valid.bind(Bindings.and(isNotNull, exists));
        } else {
            valid.bind(value.isNotNull());
        }
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
    public Property<File> valueProperty() {
        return value;
    }

    private Path getPath() {
        File file = value.get();
        return file == null ? null : file.toPath();
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
