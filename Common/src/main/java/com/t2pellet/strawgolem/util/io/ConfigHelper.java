package com.t2pellet.strawgolem.util.io;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ConfigHelper {

    public static ConfigHelper INSTANCE = new ConfigHelper();

    private final Map<Class<? extends Config>, Config> configMap;

    private ConfigHelper() {
        configMap = new HashMap<>();
    }

    public <T extends Config> void register(Supplier<T> configSupplier) throws IOException, IllegalAccessException {
        T config = configSupplier.get();
        config.load();
        configMap.put(config.getClass(), config);
    }

    public <T extends Config> void save(Class<T> configClass) throws IOException, IllegalAccessException {
        configMap.get(configClass).save();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Section {
        String value();

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @interface Comment {
            String value();
        }
    }


}
