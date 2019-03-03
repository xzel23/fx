package com.dua3.fx.util.controls;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

import com.dua3.fx.util.controls.InputDialogPane.InputControl;

public interface InputBuilder<B extends InputBuilder<B>> {

    <T> B add(String id, String label, Class<T> type, T dflt, InputControl<T> control);

    B columns(int columns);

    B text(String id, String label, String dflt);

    B text(String id, String label, String dflt, Function<String, Optional<String>> validate);

    B integer(String id, String label, Integer dflt);

    B integer(String id, String label, Integer dflt, IntFunction<Optional<String>> validate);

    B decimal(String id, String label, Double dflt);

    B decimal(String id, String label, Double dflt, DoubleFunction<Optional<String>> validate);

    B checkBox(String id, String label, boolean dflt, String text);

    <T> B comboBox(String id, String label, T dflt, Class<T> cls, Collection<T> items);
    
    @SuppressWarnings("unchecked")
    default <T> B comboBox(String id, String label, T dflt, Class<T> cls, T... items) {
        return comboBox(id, label, dflt, cls, Arrays.asList(items));
    }

    <T> B radioList(String id, String label, T dflt, Class<T> cls, Collection<T> items);

    @SuppressWarnings("unchecked")
    default <T> B radioList(String id, String label, T dflt, Class<T> cls, T... items) {
        return radioList(id, label, dflt, cls, Arrays.asList(items));
    }

}