package com.t2pellet.strawgolem.util.io;

public interface StringConverter {

    static StringConverter of(String value) {
        return new StringConverterImpl(value);
    }

    <T> T convert(Class<T> clazz);

}
