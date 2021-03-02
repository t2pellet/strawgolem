package com.commodorethrawn.strawgolem.util.io;

public interface StringConverter {

    public static StringConverter of(String value) {
        return new StringConverterImpl(value);
    }

    public <T> T convert(Class<T> clazz);

}
