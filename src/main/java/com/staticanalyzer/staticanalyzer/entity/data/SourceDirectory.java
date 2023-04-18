package com.staticanalyzer.staticanalyzer.entity.data;

import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Data
public class SourceDirectory {
    private String name;
    private Map<String, SourceDirectory> directories = new HashMap<>();
    private Map<String, SourceFile> files = new HashMap<>();

    public void addFile(String path, String content) {
        String[] parts = name.split("/");
        if (parts.length == 1) {
            SourceFile file = new SourceFile();
            file.setName(name);
            file.setPath(baseDir);
            file.setContent(content);
            files.put(name, file);
        } else {
            String dirName = parts[0];
            String newBaseDir;
            if (baseDir.length() == 0)
                newBaseDir = dirName;
            else
                newBaseDir = baseDir + "/" + dirName;
            SourceDirectory dir = directories.get(dirName);
            if (dir == null) {
                dir = new SourceDirectory();
                dir.setName(dirName);
                directories.put(dirName, dir);
            }
            String newName = String.join("/", Arrays.copyOfRange(parts, 1, parts.length));
            dir.addFile(newBaseDir, newName, content);
        }
    }

    public void addDirectory(String name) {
        String[] parts = name.split("/");
        if (parts.length == 1) {
            if (name.endsWith("/"))
                name = name.substring(0, name.length() - 1);
            SourceDirectory dir = directories.get(name);
            if (dir == null) {
                dir = new SourceDirectory();
                dir.setName(name);
                directories.put(name, dir);
            }
        } else {
            String dirName = parts[0];
            SourceDirectory dir = directories.get(dirName);
            if (dir == null) {
                dir = new SourceDirectory();
                dir.setName(dirName);
                directories.put(dirName, dir);
            }
            String newName = String.join("/", Arrays.copyOfRange(parts, 1, parts.length));
            dir.addDirectory(newName);
        }
    }

    public void setAnalyseResults(String analyseResponse) {

    }
}
