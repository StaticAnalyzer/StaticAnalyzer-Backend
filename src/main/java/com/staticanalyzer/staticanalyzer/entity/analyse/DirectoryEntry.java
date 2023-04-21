package com.staticanalyzer.staticanalyzer.entity.analyse;

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

    public DirectoryEntry<T> at(String path) {
        if (path.length() == 0)
            return this;

        String[] parts = path.split("/", 1);
        String parentDirectoryName = parts[0];

        DirectoryEntry<T> parentDirectory = directories.get(parentDirectoryName);
        if (parentDirectory == null) {
            parentDirectory = new DirectoryEntry<T>();
            parentDirectory.setName(parentDirectoryName);
            directories.put(parentDirectoryName, parentDirectory);
        }

        if (parts.length == 1)
            return parentDirectory;

        String subDirectoryPath = parts[2];
        return parentDirectory.at(subDirectoryPath);
    }

    public void addFile(String filename, T file) {
        files.put(filename, file);
    }

    public void getFile(String )
}
