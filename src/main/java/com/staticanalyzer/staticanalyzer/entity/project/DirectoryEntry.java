package com.staticanalyzer.staticanalyzer.entity.project;

import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 目录单元
 * 用于结构化显示项目
 * 
 * @author WLLEGit
 * @since 0.0.2
 * @see com.staticanalyzer.staticanalyzer.entity.project.FileEntry
 */
@Data
@ApiModel(description = "目录单元")
public class DirectoryEntry<F extends FileEntry> {

    /* 目录名 */
    @ApiModelProperty(value = "目录名", required = true)
    private String name;

    /* 子目录集 */
    @ApiModelProperty(value = "子目录集", required = false)
    private Map<String, DirectoryEntry<F>> directories = new HashMap<>();

    /* 子文件集 */
    @ApiModelProperty(value = "文件集", required = false)
    private Map<String, F> files = new HashMap<>();

    /**
     * 添加一个文件
     * 
     * @apiNote 文件名为相对目录
     * @param fileEntry
     */
    public void addFileEntry(String path, F fileEntry) {
        Path filePath = Paths.get(path);
        DirectoryEntry<F> directoryEntry = this;
        for (Path currentPath : filePath.getParent()) {
            Map<String, DirectoryEntry<F>> directories = directoryEntry.getDirectories();
            /* 将directoryEntry递进 */
            if ((directoryEntry = directories.get(currentPath.toString())) == null) {
                directoryEntry = new DirectoryEntry<>();
                directories.put(currentPath.toString(), directoryEntry);
            }
        }
        directoryEntry.getFiles().put(filePath.getFileName().toString(), fileEntry);
    }
}
