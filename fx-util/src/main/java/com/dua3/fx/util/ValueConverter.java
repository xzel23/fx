package com.dua3.fx.util;

import com.dua3.utility.options.Option;
import com.dua3.utility.options.Option.Value;

public class ValueConverter<T> {

    public Value<T> toValue(T t) {
        return Option.value(t);
    }

    public T fromValue(Value<T>v) {
        return v.get();
    }

}
