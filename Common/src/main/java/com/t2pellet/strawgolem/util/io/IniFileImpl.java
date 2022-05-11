package com.t2pellet.strawgolem.util.io;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class IniFileImpl implements IniFile {

    private final Pattern sectionPattern = Pattern.compile("^\\[.*\\]$");
    private final Pattern valuePattern = Pattern.compile(".* = .*");
    Map<String, IniFile.Section> sections = new LinkedHashMap<>();

    @Override
    public void load(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = bufferedReader.readLine();
        Section lastSection = null;
        while (line != null) {
            if (sectionPattern.matcher(line).matches()) {
                String name = line.substring(1, line.length() - 1);
                lastSection = new Section(name);
                sections.put(name, lastSection);
            } else if (valuePattern.matcher(line).matches()) {
                int equalIdx = line.indexOf('=');
                String key = line.substring(0, equalIdx).trim();
                String val = line.substring(equalIdx + 1).trim();
                lastSection.add(key, val);
            }
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
    }

    @Override
    public void store(File file) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        for (IniFile.Section v : sections.values()) {
            Section value = (Section) v;
            writer.write("[" + value.name + "]" + '\n');
            value.keyValueMap.forEach((key, val) -> {
                String comment = value.keyCommentMap.get(key);
                if (comment != null) writer.write("#" + comment + '\n');
                writer.write(key + " = " + val + '\n');
            });
            writer.write('\n');
        }
        writer.close();
    }

    @Override
    public IniFile.Section getSection(String name) {
        return sections.get(name);
    }

    @Override
    public Section addSection(String name) {
        Section section = new Section(name);
        sections.put(name, section);
        return section;
    }

    public static class Section implements IniFile.Section {

        private final String name;
        private final Map<String, String> keyValueMap = new LinkedHashMap<>();
        private final Map<String, String> keyCommentMap = new HashMap<>();

        private Section(String name) {
            this.name = name;
        }

        @Override
        public void add(String key, Object val) {
            keyValueMap.put(key, String.valueOf(val));
        }

        @Override
        public void comment(String key, String comment) {
            keyCommentMap.put(key, comment);
        }

        @Override
        public <T> T get(String key, Class<T> clazz) {
            if (keyValueMap.containsKey(key)) {
                return StringConverter.of(keyValueMap.get(key)).convert(clazz);
            }
            return null;
        }

        @Override
        public <T> List<T> getAll(String key, Class<T> clazz) {
            if (keyValueMap.containsKey(key)) {
                String listStr = keyValueMap.get(key);
                listStr = listStr.substring(1, listStr.length() - 1);
                String[] strArr = listStr.isEmpty() ? new String[]{} : listStr.split(",");
                return Arrays.stream(strArr).map(e -> StringConverter.of(e).convert(clazz)).collect(Collectors.toList());
            }
            return null;
        }

    }

}
