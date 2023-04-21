package com.staticanalyzer.staticanalyzer.entity.analyse;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

@Data
@ApiModel(description = "目录解析结构")
public class DirectoryEntry<T> {

    @ApiModelProperty(value = "目录名", required = true)
    private String name;

    @ApiModelProperty(value = "子目录", required = false)
    private Map<String, DirectoryEntry<T>> directories = new HashMap<>();

    @ApiModelProperty(value = "文件", required = false)
    private Map<String, T> files = new HashMap<>();

    private DirectoryEntry<T> create(Path directoryPath) {
        DirectoryEntry<T> directoryEntry = new DirectoryEntry<T>();
        directoryEntry.setName(directoryPath.toString());
        directories.put(directoryPath.toString(), directoryEntry);
        return directoryEntry;
    }

    private DirectoryEntry<T> createIfAbsent(Path directoryPath) {
        DirectoryEntry<T> directoryEntry = directories.get(directoryPath.toString());
        if (directoryEntry == null)
            return create(directoryPath);
        return directoryEntry;
    }

    public DirectoryEntry<T> createAbsolute(String fullDirectoryName) {
        Path directoryPath = Path.of(fullDirectoryName);
        return createAbsolute(directoryPath);
    }

    public DirectoryEntry<T> createAbsolute(Path fullDirectoryPath) {
        DirectoryEntry<T> directoryEntry = this;
        if (fullDirectoryPath != null)
            for (Path ancestor : fullDirectoryPath)
                directoryEntry = directoryEntry.createIfAbsent(ancestor);
        return directoryEntry;
    }

    public DirectoryEntry<T> getEntryAt(String fullDirectoryName) {
        Path directoryPath = Path.of(fullDirectoryName);
        return getEntryAt(directoryPath);
    }

    public DirectoryEntry<T> getEntryAt(Path fullDirectoryPath) {
        DirectoryEntry<T> directoryEntry = this;
        if (fullDirectoryPath != null)
            for (Path ancestor : fullDirectoryPath) {
                directoryEntry = directoryEntry.getDirectories().get(ancestor.toString());
                if (directoryEntry == null)
                    return null;
            }
        return directoryEntry;
    }

    public void addFile(String fullFileName, T file) {
        Path filePath = Path.of(fullFileName);
        DirectoryEntry<T> parentEntry = createAbsolute(filePath.getParent());
        parentEntry.getFiles().put(filePath.getFileName().toString(), file);
    }

    public T getFileAt(String fullFileName) {
        Path filePath = Path.of(fullFileName);
        DirectoryEntry<T> parentEntry = getEntryAt(filePath.getParent());
        if (parentEntry == null)
            return null;
        return parentEntry.getFiles().get(filePath.getFileName().toString());
    }

    public static DirectoryEntry<FileEntryVO> visualize(DirectoryEntry<FileEntry> root) {
        return null;
    }
}
