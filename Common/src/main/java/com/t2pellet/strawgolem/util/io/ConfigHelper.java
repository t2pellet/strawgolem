package com.t2pellet.strawgolem.util.io;

import com.t2pellet.strawgolem.platform.Services;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;

public class ConfigHelper {

    public static final ConfigHelper INSTANCE = new ConfigHelper();

    private HashMap<String, Config> configs = new HashMap<>();

    private ConfigHelper() {
    }

    public <T extends Config> void register(String modid, T config) throws IOException, IllegalAccessException {
        config.load();
        configs.put(modid, config);
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
