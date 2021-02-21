package com.commodorethrawn.strawgolem.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IniFile {

    public static IniFile newInstance() {
        return new IniFileImpl();
    }

    public void load(File file) throws IOException;

    public void store(File file) throws IOException;

    public Section getSection(String name);

    public Section addSection(String name);

    public void clear();

    public interface Section {

        public void add(String key, Object value);

        public <T> T get(String key, Class<T> clazz);

        public List<String> getAll(String key);

        public void clear();
    }
}
