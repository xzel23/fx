// Copyright 2019 Axel Howind
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.dua3.fx.controls;

import com.dua3.cabe.annotations.Nullable;
import com.dua3.utility.data.Pair;
import com.dua3.utility.options.Arguments;
import com.dua3.utility.options.Option;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Builder for Alert Dialogs.
 * <p>
 * Provides a fluent interface to create Alerts.
 */
public class InputPaneBuilder
        extends AbstractPaneBuilder<InputPane, InputPaneBuilder, Map<String, Object>>
        implements InputBuilder<InputPaneBuilder> {

    private final InputGridBuilder pb = new InputGridBuilder();
    private final List<Pair<ButtonType, Consumer<InputDialogPane<Map<String, Object>>>>> buttons = new ArrayList<>();

    InputPaneBuilder() {
        super();
        setDialogSupplier(() -> new InputPane(pb.build()));
    }

    @Override
    public <T> InputPaneBuilder add(String id, String label, Class<T> type, Supplier<T> dflt, InputControl<T> control) {
        pb.add(id, label, type, dflt, control);
        return this;
    }

    @Override
    public <T> InputPaneBuilder add(String id, Class<T> type, Supplier<T> dflt, InputControl<T> control) {
        pb.add(id, type, dflt, control);
        return this;
    }

    @Override
    public InputPaneBuilder addNode(String id, String label, Node node) {
        pb.addNode(id, label, node);
        return this;
    }

    @Override
    public InputPaneBuilder addNode(String id, Node node) {
        pb.addNode(id, node);
        return this;
    }

    @Override
    public InputPaneBuilder columns(int columns) {
        pb.columns(columns);
        return this;
    }

    @Override
    public InputPaneBuilder string(String id, String label, Supplier<String> dflt, Function<String, Optional<String>> validate) {
        pb.string(id, label, dflt, validate);
        return this;
    }

    @Override
    public InputPaneBuilder integer(String id, String label, Supplier<Integer> dflt, Function<Integer, Optional<String>> validate) {
        pb.integer(id, label, dflt, validate);
        return this;
    }

    @Override
    public InputPaneBuilder decimal(String id, String label, Supplier<Double> dflt, Function<Double, Optional<String>> validate) {
        pb.decimal(id, label, dflt, validate);
        return this;
    }

    @Override
    public InputPaneBuilder checkBox(String id, String label, Supplier<Boolean> dflt, String text) {
        pb.checkBox(id, label, dflt, text);
        return this;
    }

    @Override
    public <T> InputPaneBuilder comboBox(String id, String label, Supplier<T> dflt, Class<T> cls, Collection<T> items) {
        pb.comboBox(id, label, dflt, cls, items);
        return this;
    }

    @Override
    public <T> InputPaneBuilder comboBoxEx(
            String id,
            String label,
            @Nullable UnaryOperator<T> edit,
            @Nullable Supplier<T> add,
            @Nullable BiPredicate<ComboBoxEx<T>, T> remove,
            Function<T,String> format,
            Supplier<T> dflt,
            Class<T> cls,
            Collection<T> items) {
        pb.comboBoxEx(id, label, edit, add, remove, format, dflt, cls, items);
        return this;
    }

    @Override
    public <T> InputPaneBuilder radioList(String id, String label, Supplier<T> dflt, Class<T> cls, Collection<T> items) {
        pb.radioList(id, label, dflt, cls, items);
        return this;
    }

    @Override
    public InputPaneBuilder options(String id, String label, Supplier<Arguments> dflt, Supplier<Collection<Option<?>>> options) {
        pb.options(id, label, dflt, options);
        return this;
    }

    @Override
    public InputPaneBuilder options(String id, Supplier<Arguments> dflt, Supplier<Collection<Option<?>>> options) {
        pb.options(id, dflt, options);
        return this;
    }

    @Override
    public InputPaneBuilder chooseFile(String id, String label, Supplier<Path> dflt, FileDialogMode mode, FileChooser.ExtensionFilter filter) {
        pb.chooseFile(id, label, dflt, mode, filter);
        return this;
    }

    public InputPaneBuilder button(ButtonType b, Consumer<InputDialogPane<Map<String, Object>>> action) {
        buttons.add(Pair.of(b, action));
        return this;
    }

}
