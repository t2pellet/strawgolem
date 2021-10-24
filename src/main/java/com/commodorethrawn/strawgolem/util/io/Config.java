package com.commodorethrawn.strawgolem.util.io;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public abstract class Config {

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

    private final IniFile iniFile;
    private final File configFile;

    public Config(String modId) throws IOException, IllegalAccessException {
        iniFile = IniFile.newInstance();
        String configPath = FabricLoader.getInstance().getConfigDir().toString() + "/" + modId + ".ini";
        configFile = new File(configPath);
        load();
    }

    /**
     * Save the Config to disk
     * @throws IOException if there is an error writing to disk
     * @throws IllegalAccessException if there is an error accessing config elements
     */
    public void save() throws IOException, IllegalAccessException {
        for (Class<?> aClass : this.getClass().getDeclaredClasses()) {
            if (aClass.isAnnotationPresent(Section.class)) {
                Section section = aClass.getAnnotation(Section.class);
                IniFile.Section s = iniFile.addSection(section.value());
                for (Field declaredField : aClass.getDeclaredFields()) {
                    declaredField.setAccessible(true);
                    s.add(declaredField.getName(), declaredField.get(null));
                    if (declaredField.isAnnotationPresent(Section.Comment.class)) {
                        Section.Comment comment = declaredField.getAnnotation(Section.Comment.class);
                        s.comment(declaredField.getName(), comment.value());
                    }
                }
            }
        }
        iniFile.store(configFile);
    }

    /**
     * Loads the Config from disk
     * @throws IOException if there is an error reading from disk
     * @throws IllegalAccessException if there is an error accessing config elements
     */
    public void load() throws IOException, IllegalAccessException {
        if (configFile.createNewFile()) {
            save();
        }
        iniFile.load(configFile);
        Class<?>[] declaredClasses = this.getClass().getDeclaredClasses();
        for (Class<?> declaredClass : declaredClasses) {
            if (declaredClass.isAnnotationPresent(Section.class)) {
                Section section = declaredClass.getAnnotation(Section.class);
                IniFile.Section s = iniFile.getSection(section.value());
                for (Field declaredField : declaredClass.getDeclaredFields()) {
                    Object value;
                    if (List.class.isAssignableFrom(declaredField.getType())) {
                        Type listType = declaredField.getGenericType();
                        if (listType instanceof ParameterizedType) {
                            Class<?> type = (Class<?>) ((ParameterizedType) listType).getActualTypeArguments()[0];
                            value = s.getAll(declaredField.getName(), type);
                        } else throw new IllegalArgumentException("Invalid list type: " + listType);
                    } else value = s.get(declaredField.getName(), declaredField.getType());
                    if (value != null) setField(declaredField, value);
                }
            }
        }
    }

    private static void setField(Field field, Object value) throws IllegalAccessException {
            field.setAccessible(true);
            field.set(null, value);
    }

}
