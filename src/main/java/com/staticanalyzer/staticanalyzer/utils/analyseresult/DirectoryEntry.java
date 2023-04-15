package com.staticanalyzer.staticanalyzer.utils.analyseresult;

import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DirectoryEntry {
    private String name;
    private Map<String, DirectoryEntry> directories = new HashMap<>();
    private Map<String, FileEntry> files = new HashMap<>();

    public void addFile(String baseDir, String name, String content) {
        String[] parts = name.split("/");
        if (parts.length == 1) {
            FileEntry file = new FileEntry();
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
            DirectoryEntry dir = directories.get(dirName);
            if (dir == null) {
                dir = new DirectoryEntry();
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
            DirectoryEntry dir = directories.get(name);
            if (dir == null) {
                dir = new DirectoryEntry();
                dir.setName(name);
                directories.put(name, dir);
            }
        } else {
            String dirName = parts[0];
            DirectoryEntry dir = directories.get(dirName);
            if (dir == null) {
                dir = new DirectoryEntry();
                dir.setName(dirName);
                directories.put(dirName, dir);
            }
            String newName = String.join("/", Arrays.copyOfRange(parts, 1, parts.length));
            dir.addDirectory(newName);
        }
    }
}
