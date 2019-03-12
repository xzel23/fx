package com.dua3.fx.util;

import java.util.Objects;

import com.dua3.utility.options.Option;
import com.dua3.utility.options.Option.Value;

import javafx.util.StringConverter;

public class StringValueConverter extends StringConverter<Value<String>> {

    private static StringValueConverter INSTANCE = new StringValueConverter();

    public static StringValueConverter instance() {
        return INSTANCE;
    }
    
    @Override
    public Value<String> fromString(String s) {
        return Option.value(s);
    }

    @Override
    public String toString(Value<String>v) {
        return Objects.toString(v.get(), "");
    }
}