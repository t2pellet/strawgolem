package com.t2pellet.strawgolem.util.io;

import com.t2pellet.strawgolem.platform.Services;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class Config {

    private static final String CONFIG_DIR = Services.PLATFORM.getGameDir() + "/config/";

    private final File file;


    protected Config(String modid) {
        file = new File(CONFIG_DIR + modid + ".ini");
    }

    /**
     * Save the Config to disk
     *
     * @throws IOException            if there is an error writing to disk
     * @throws IllegalAccessException if there is an error accessing config elements
     */
    void save() throws IOException, IllegalAccessException {
        IniFile iniFile = IniFile.newInstance();
        for (Class<?> aClass : this.getClass().getDeclaredClasses()) {
            if (aClass.isAnnotationPresent(ConfigHelper.Section.class)) {
                ConfigHelper.Section section = aClass.getAnnotation(ConfigHelper.Section.class);
                IniFile.Section s = iniFile.addSection(section.value());
                for (Field declaredField : aClass.getDeclaredFields()) {
                    declaredField.setAccessible(true);
                    s.add(declaredField.getName(), declaredField.get(null));
                    if (declaredField.isAnnotationPresent(ConfigHelper.Section.Comment.class)) {
                        ConfigHelper.Section.Comment comment = declaredField.getAnnotation(ConfigHelper.Section.Comment.class);
                        s.comment(declaredField.getName(), comment.value());
                    }
                }
            }
        }
        iniFile.store(file);
    }

    /**
     * Loads the Config from disk
     *
     * @throws IOException            if there is an error reading from disk
     * @throws IllegalAccessException if there is an error accessing config elements
     */
    void load() throws IOException, IllegalAccessException {
        if (file.createNewFile()) {
            save();
        }
        IniFile iniFile = IniFile.newInstance();
        iniFile.load(file);
        Class<?>[] declaredClasses = this.getClass().getDeclaredClasses();
        for (Class<?> declaredClass : declaredClasses) {
            if (declaredClass.isAnnotationPresent(ConfigHelper.Section.class)) {
                ConfigHelper.Section section = declaredClass.getAnnotation(ConfigHelper.Section.class);
                IniFile.Section s = iniFile.getSection(section.value());
                if (s != null) {
                    for (Field declaredField : declaredClass.getDeclaredFields()) {
                        Object value;
                        if (List.class.isAssignableFrom(declaredField.getType())) {
                            Type listType = declaredField.getGenericType();
                            if (listType instanceof ParameterizedType) {
                                Class<?> type = (Class<?>) ((ParameterizedType) listType).getActualTypeArguments()[0];
                                value = s.getAll(declaredField.getName(), type);
                            } else throw new IllegalArgumentException("Invalid list type: " + listType);
                        } else value = s.get(declaredField.getName(), declaredField.getType());
                        if (value != null) {
                            setField(declaredField, value);
                        }
                    }
                }
            }
        }
        save();
    }

    private static void setField(Field field, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(null, value);
    }
}
