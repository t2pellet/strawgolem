package com.t2pellet.strawgolem.util.io;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ConfigHelper {


    private ConfigHelper() {
    }

    public static <T extends Config> void register(T config) throws IOException, IllegalAccessException {
        config.load();
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
