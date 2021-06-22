package com.commodorethrawn.strawgolem.util.io;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public interface IniFile {

    static IniFile newInstance() {
        return new IniFileImpl();
    }

    /**
     * Loads given IniFile on disk
     * @param file the file to load
     * @throws IOException if file can't be read
     */
    void load(File file) throws IOException;

    /**
     * Stores the IniFile on disk
     * @param file the file to store it in
     * @throws IOException if file can't be written to
     */
    void store(File file) throws IOException;

    /**
     * Gets the section by the given name
     * @param name the section name
     * @return the section
     */
    Section getSection(String name);

    /**
     * Adds a section with the given name
     * @param name the section name
     * @return the created section
     */
    Section addSection(String name);

    interface Section {

        /**
         * Adds a KVP to the section
         * @param key the key to add
         * @param value the value to add
         */
        void add(String key, Object value);

        /**
         * Adds a comment to a given KVP
         * @param key the key to comment on
         * @param comment the comment
         */
        void comment(String key, String comment);

        /**
         * Gets the value of the given key
         * @param key the key to search
         * @param clazz the class object
         * @param <T> the object type
         * @return the value of the given key
         */
        <T> T get(String key, Class<T> clazz);

        <T> List<T> getAll(String key, Class<T> clazz);

    }
}
