package com.commodorethrawn.strawgolem.util.io;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

class IniFileImpl implements IniFile {

    private Pattern sectionPattern = Pattern.compile("^\\[.*\\]$");
    private Pattern valuePattern = Pattern.compile(".* = .*");
    private Map<String, Section> sections = new LinkedHashMap<>();

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

    public void store(File file) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        for (Section value : sections.values()) {
            bufferedWriter.write("[" + value.name + "]" + '\n');
            value.keyValueMap.forEach((key, val) -> {
                try {
                    String comment = value.keyCommentMap.get(key);
                    if (comment != null) bufferedWriter.write("#" + comment + '\n');
                    bufferedWriter.write(key + " = " + val + '\n');
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            bufferedWriter.write('\n');
        }
        bufferedWriter.close();
    }

    public Section getSection(String name) {
        return sections.get(name);
    }

    public Section addSection(String name) {
        Section section = new Section(name);
        sections.put(name, section);
        return section;
    }

    public void clear() {
        sections.clear();
    }

    public static class Section implements IniFile.Section {

        private final String name;
        private final Map<String, String> keyValueMap = new LinkedHashMap<>();
        private final Map<String, String> keyCommentMap = new HashMap<>();

        private Section(String name) {
            this.name = name;
        }

        public void add(String key, Object val) {
            keyValueMap.put(key, String.valueOf(val));
        }

        public void comment(String key, String comment) {
            keyCommentMap.put(key, comment);
        }

        public <T> T get(String key, Class<T> clazz) {
            return StringConverter.of(keyValueMap.get(key)).convert(clazz);
        }

        public List<String> getAll(String key) {
            String val = keyValueMap.get(key);
            return Arrays.asList(val.substring(1, val.length() - 1).split(","));
        }

        public void clear() {
            keyValueMap.clear();
            keyCommentMap.clear();
        }

    }

}
