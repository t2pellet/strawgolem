package com.commodorethrawn.strawgolem.util;


class StringConverterImpl implements StringConverter{

    private static final String INTEGER = "java.lang.Integer";
    private static final String BOOLEAN = "java.lang.Boolean";
    private static final String DOUBLE = "java.lang.Double";
    private static final String FLOAT = "java.lang.Float";

    private String value;

    public StringConverterImpl(String value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public <T> T convert(Class<T> clazz) {
        switch (clazz.getTypeName()) {
            case INTEGER:
                return (T) (Object) Integer.parseInt(value);
            case BOOLEAN:
                return (T) (Object) Boolean.parseBoolean(value);
            case FLOAT:
                return (T) (Object) Float.parseFloat(value);
            case DOUBLE:
                return (T) (Object) Double.parseDouble(value);
            default:
                throw new IllegalArgumentException("Invalid type: " + clazz.getTypeName());
        }
    }

}
