package com.dua3.fx.controls;

import com.dua3.fx.util.FxUtil;
import javafx.beans.binding.Bindings;
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
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class FileInput extends CustomControl<HBox> implements InputControl<Path> {

    private static final StringConverter<Path> PATH_CONVERTER = new PathConverter();

    static class PathConverter extends StringConverter<Path> {
        @Override
        public String toString(Path path) {
            return path == null ? "" : path.toString();
        }

        @Override
        public Path fromString(String s) {
            return s == null ? Paths.get("") : Paths.get(s);
        }
    }

    private final TextField tfFilename;
    private final Button button;

    private final ObjectProperty<Path> value = new SimpleObjectProperty<>();

    private final FileDialogMode mode;
    private final FileChooser.ExtensionFilter[] filters;
    private final Supplier<Path> dflt;
    private boolean existingOnly = true;

    private final StringProperty error = new SimpleStringProperty("");
    private final BooleanProperty valid = new SimpleBooleanProperty(true);

    public FileInput(FileDialogMode mode, Supplier<Path> dflt, Collection<FileChooser.ExtensionFilter> filters) {
        this(mode, dflt, filters.toArray(FileChooser.ExtensionFilter[]::new));
    }

    public FileInput(FileDialogMode mode, Supplier<Path> dflt, FileChooser.ExtensionFilter... filters) {
        super(new HBox());

        getStyleClass().setAll("file-input");

        this.mode = Objects.requireNonNull(mode);
        this.filters = Arrays.copyOf(Objects.requireNonNull(filters), filters.length);
        this.dflt = Objects.requireNonNull(dflt);

        this.tfFilename = new TextField();
        this.button = new Button("…");

        HBox.setHgrow(tfFilename, Priority.ALWAYS);

        button.setOnAction(evt -> {

            Path initialDir = value.get();
            if (initialDir != null && !Files.isDirectory(initialDir)) {
                initialDir = initialDir.getParent();
            }
            if (initialDir == null) {
                initialDir = Paths.get(".");
            }

            switch (this.mode) {
                case OPEN -> Dialogs.chooseFile()
                        .initialDir(initialDir)
                        .filter(this.filters)
                        .showOpenDialog(null)
                        .ifPresent(value::setValue);
                case SAVE -> Dialogs.chooseFile()
                        .initialDir(initialDir)
                        .filter(this.filters)
                        .showSaveDialog(null)
                        .ifPresent(value::setValue);
                case DIRECTORY -> Dialogs.chooseDirectory()
                        .initialDir(initialDir)
                        .showDialog(null)
                        .ifPresent(value::setValue);
            }
        });

        container.getChildren().setAll(tfFilename, button);

        tfFilename.textProperty().bindBidirectional(valueProperty(), PATH_CONVERTER);

        // error property
        StringExpression errorText = Bindings.createStringBinding(
                () -> {
                    Path file = value.get();
                    if (file == null) {
                        return "No file selected.";
                    }
                    if (mode == FileDialogMode.OPEN && !Files.exists(file)) {
                        return "File does not exist: " + file;
                    }
                    return "";
                },
                value
        );

        error.bind(errorText);

        // valid property
        valid.bind(Bindings.createBooleanBinding(() -> {
                            Path p = getPath();

                            if (p == null) {
                                return false;
                            }

                            boolean exists = Files.exists(p);
                            boolean isDirectory = Files.isDirectory(p);

                            return switch (mode) {
                                case DIRECTORY ->
                                    // is a directory or existingOnly is not set and doesn't exist
                                        isDirectory || (!existingOnly && !exists);
                                case OPEN, SAVE ->
                                    // is no directory, and existingOnly is not set or exists
                                        !isDirectory && (!existingOnly || exists);
                            };
                        },
                        value
                )
        );

        // enable drag&drop
        Function<List<Path>, List<TransferMode>> acceptPath = list ->
                list.isEmpty() ? Collections.emptyList() : List.of(TransferMode.MOVE);
        tfFilename.setOnDragOver(FxUtil.dragEventHandler(acceptPath));
        tfFilename.setOnDragDropped(FxUtil.dropEventHandler(list -> valueProperty().setValue(list.get(0))));

        // set initial path
        Path p = dflt.get();
        if (p != null) {
            set(p);
        }
    }

    public void setExistingOnly(boolean existingOnly) {
        this.existingOnly = existingOnly;
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
