package com.dua3.fx.util.controls;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import com.dua3.utility.options.OptionSet;
import com.dua3.utility.options.OptionValues;

public interface InputBuilder<B extends InputBuilder<B>> {

    <T> B add(String id, String label, Class<T> type, T dflt, InputControl<T> control);

    B columns(int columns);

    default B text(String id, String label, String dflt) {
        return text(id, label, dflt, s -> Optional.empty());
    }
    
    B text(String id, String label, String dflt, Function<String, Optional<String>> validate);

    default B integer(String id, String label, Integer dflt) {
        return integer(id, label, dflt, i -> Optional.empty());
    }

    B integer(String id, String label, Integer dflt, IntFunction<Optional<String>> validate);

    default B decimal(String id, String label, Double dflt) {
        return decimal(id, label, dflt, d -> Optional.empty());
    }
    
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

    B options(String id, String label, Supplier<OptionSet> options, Supplier<OptionValues> dflt);
    
    default B options(String id, String label, OptionSet options, OptionValues dflt) {
        return options(id, label, () -> options, () -> dflt);
    }
    
}