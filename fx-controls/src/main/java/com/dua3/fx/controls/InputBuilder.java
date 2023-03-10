package com.dua3.fx.controls;

import com.dua3.utility.options.Arguments;
import com.dua3.utility.options.Option;
import javafx.scene.Node;
import javafx.stage.FileChooser;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface InputBuilder<B extends InputBuilder<B>> {

    /**
     * Add labeled input control.
     *
     * @param <T>     the result type
     * @param id      the control's ID
     * @param label   the label text
     * @param type    the result type
     * @param dflt    supplier of default value
     * @param control the control
     * @return {@code this}
     */
    <T> B add(String id, String label, Class<T> type, Supplier<T> dflt, InputControl<T> control);

    /**
     * Add unlabeled input control.
     *
     * @param <T>     the result type
     * @param id      the control's ID
     * @param type    the result type
     * @param dflt    supplier of default value
     * @param control the control
     * @return {@code this}
     */
    <T> B add(String id, Class<T> type, Supplier<T> dflt, InputControl<T> control);

    /**
     * Add labeled input control.
     *
     * @param id    the node's ID
     * @param label the label text
     * @param node  the node
     * @return {@code this}
     */
    B addNode(String id, String label, Node node);

    /**
     * Add unlabeled input control.
     *
     * @param id   the node's ID
     * @param node the node
     * @return {@code this}
     */
    B addNode(String id, Node node);

    /**
     * Set number of columns for layout (default is 1).
     *
     * @param columns the number of columns for laying out the input controls
     * @return {@code this}
     */
    B columns(int columns);

    /**
     * Add labeled string input.
     *
     * @param id    the ID
     * @param label the label text
     * @param dflt  supplier of default value
     * @return {@code this}
     */
    default B string(String id, String label, Supplier<String> dflt) {
        return string(id, label, dflt, s -> Optional.empty());
    }

    /**
     * Add labeled string input.
     *
     * @param id       the ID
     * @param label    the label text
     * @param dflt     supplier of default value
     * @param validate validation callback, return error message if invalid, empty optional if valid
     * @return {@code this}
     */
    B string(String id, String label, Supplier<String> dflt, Function<String, Optional<String>> validate);

    /**
     * Add labeled integer input.
     *
     * @param id    the ID
     * @param label the label text
     * @param dflt  supplier of default value
     * @return {@code this}
     */
    default B integer(String id, String label, Supplier<Integer> dflt) {
        return integer(id, label, dflt, i -> Optional.empty());
    }

    /**
     * Add labeled integer input.
     *
     * @param id       the ID
     * @param label    the label text
     * @param dflt     supplier of default value
     * @param validate validation callback, return error message if invalid, empty optional if valid
     * @return {@code this}
     */
    B integer(String id, String label, Supplier<Integer> dflt, Function<Integer, Optional<String>> validate);

    /**
     * Add labeled decimal input.
     *
     * @param id    the ID
     * @param label the label text
     * @param dflt  supplier of default value
     * @return {@code this}
     */
    default B decimal(String id, String label, Supplier<Double> dflt) {
        return decimal(id, label, dflt, d -> Optional.empty());
    }

    /**
     * Add labeled decimal input.
     *
     * @param id       the ID
     * @param label    the label text
     * @param dflt     supplier of default value
     * @param validate validation callback, return error message if invalid, empty optional if valid
     * @return {@code this}
     */
    B decimal(String id, String label, Supplier<Double> dflt, Function<Double, Optional<String>> validate);

    /**
     * Add labeled checkbox.
     *
     * @param id    the ID
     * @param label the label text
     * @param dflt  supplier of default value
     * @param text  the checkbox text
     * @return {@code this}
     */
    B checkBox(String id, String label, Supplier<Boolean> dflt, String text);

    /**
     * Add labeled combobox.
     *
     * @param <T>   the item type
     * @param id    the ID
     * @param label the label text
     * @param dflt  supplier of default value
     * @param cls   the result class
     * @param items the items to choose from
     * @return {@code this}
     */
    <T> B comboBox(String id, String label, Supplier<T> dflt, Class<T> cls, Collection<T> items);

    /**
     * Add labeled list of radiobuttons.
     *
     * @param <T>   the item type
     * @param id    the ID
     * @param label the label text
     * @param dflt  supplier of default value
     * @param cls   the result class
     * @param items the items to choose from
     * @return {@code this}
     */
    <T> B radioList(String id, String label, Supplier<T> dflt, Class<T> cls, Collection<T> items);

    /**
     * Add labeled pane with options.
     *
     * @param id      the ID
     * @param label   the label text
     * @param dflt    supplier of default values
     * @param options supplier of options
     * @return {@code this}
     */
    B options(String id, String label, Supplier<Arguments> dflt, Supplier<Collection<Option<?>>> options);

    /**
     * Add unlabeled pane with options.
     * <p>
     * <em>Note to implementers:</em> Labels of the options should be aligned properly with labels of the input dialog.
     * </p>
     *
     * @param id      the ID
     * @param dflt    supplier of default values
     * @param options supplier of options
     * @return {@code this}
     */
    B options(String id, Supplier<Arguments> dflt, Supplier<Collection<Option<?>>> options);

    /**
     * Add File chooser.
     *
     * @param id     the ID
     * @param label  the label text
     * @param dflt   supplier of default value
     * @param mode   the mode, either {@link FileDialogMode#OPEN} or {@link FileDialogMode#SAVE}
     * @param filter the extension filter to use
     * @return {@code this}
     */
    B chooseFile(String id, String label, Supplier<Path> dflt, FileDialogMode mode, FileChooser.ExtensionFilter filter);

    enum FileDialogMode {
        OPEN,
        SAVE
    }

}
