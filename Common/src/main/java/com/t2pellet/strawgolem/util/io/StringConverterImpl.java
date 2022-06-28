package com.t2pellet.strawgolem.util.io;


class StringConverterImpl implements StringConverter {

    private static final String INTEGER = "int";
    private static final String BOOLEAN = "boolean";
    private static final String DOUBLE = "double";
    private static final String FLOAT = "float";
    private static final String STRING = "java.lang.String";

    private final String value;

    public StringConverterImpl(String value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public <T> T convert(Class<T> clazz) {
        return switch (clazz.getTypeName()) {
            case INTEGER -> (T) (Object) Integer.parseInt(value);
            case BOOLEAN -> (T) (Object) Boolean.parseBoolean(value);
            case FLOAT -> (T) (Object) Float.parseFloat(value);
            case DOUBLE -> (T) (Object) Double.parseDouble(value);
            case STRING -> (T) value;
            default -> throw new IllegalArgumentException("Cannot convert to type: " + clazz.getTypeName());
        };
    }

}
