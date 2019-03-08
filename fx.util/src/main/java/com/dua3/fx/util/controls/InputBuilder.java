package com.dua3.fx.util.controls;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.dua3.utility.options.OptionSet;
import com.dua3.utility.options.OptionValues;

public interface InputBuilder<B extends InputBuilder<B>> {

    <T> B add(String id, String label, Class<T> type, Supplier<T> dflt, InputControl<T> control);

    B columns(int columns);

    default B string(String id, String label, Supplier<String> dflt) {
        return string(id, label, dflt, s -> Optional.empty());
    }
    
    B string(String id, String label, Supplier<String> dflt, Function<String, Optional<String>> validate);

    default B integer(String id, String label, Supplier<Integer> dflt) {
        return integer(id, label, dflt, i -> Optional.empty());
    }

    B integer(String id, String label, Supplier<Integer> dflt, Function<Integer,Optional<String>> validate);

    default B decimal(String id, String label, Supplier<Double> dflt) {
        return decimal(id, label, dflt, d -> Optional.empty());
    }
    
    B decimal(String id, String label, Supplier<Double> dflt, Function<Double,Optional<String>> validate);

    B checkBox(String id, String label, Supplier<Boolean> dflt, String text);

    <T> B comboBox(String id, String label, Supplier<T> dflt, Class<T> cls, Collection<T> items);
    
    <T> B radioList(String id, String label, Supplier<T> dflt, Class<T> cls, Collection<T> items);

    B options(String id, String label, Supplier<OptionValues> dflt, Supplier<OptionSet> options);
    
}