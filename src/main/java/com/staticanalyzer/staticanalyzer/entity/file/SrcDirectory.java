package com.staticanalyzer.staticanalyzer.entity.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 源文件目录类
 * <p>
 * 提供{@code SrcFile}及其子类的结构化展示
 * </p>
 * 
 * @see SrcFile
 * @author YangYu
 * @since 0.2
 */
@lombok.Getter
@lombok.Setter
@ApiModel(description = "源文件目录类")
public class SrcDirectory {

    @ApiModelProperty(value = "目录名", required = true)
    private String name;

    @ApiModelProperty(value = "子目录集", required = false)
    private java.util.Map<String, SrcDirectory> directories;

    @ApiModelProperty(value = "文件集", required = false)
    private java.util.Map<String, SrcFile> files;

    public SrcDirectory() {
        this.name = "root";
        this.directories = new java.util.HashMap<>();
        this.files = new java.util.HashMap<>();
    }

    /**
     * 添加一个源文件，路径中的目录将被自动创建
     * 
     * @param path    源文件的相对路径
     * @param srcFile 源文件对象
     * @return 成功添加返回{@code true}
     *         源文件名不存在返回{@code false}
     * @since 0.2
     */
    public boolean addSrcFile(String path, SrcFile srcFile) {
        if (path.startsWith("/"))
            path = path.substring(1);

        java.nio.file.Path relativePath = java.nio.file.Path.of(path);
        java.nio.file.Path relativeDirectoryPath = relativePath.getParent();
        String srcFileName = relativePath.getFileName().toString();
        SrcDirectory srcDirectory = this;

        if (srcFileName.length() == 0)
            return false;

        if (relativeDirectoryPath == null) {
            this.files.put(srcFileName, srcFile);
            return true;
        }

        for (java.nio.file.Path currDirectoryPath : relativeDirectoryPath) {
            java.util.Map<String, SrcDirectory> directories = srcDirectory.getDirectories();
            String srcDirectoryName = currDirectoryPath.toString();
            srcDirectory = directories.get(srcDirectoryName);
            if (srcDirectory == null) {
                srcDirectory = new SrcDirectory();
                srcDirectory.setName(srcDirectoryName);
                directories.put(srcDirectoryName, srcDirectory);
            }
        }

        srcDirectory.getFiles().put(srcFileName, srcFile);
        return true;
    }

    /**
     * 移除一个源文件
     * 
     * @param path 源文件的相对路径
     * @return 找不到源文件返回{@code false}
     *         成功移除返回{@code true}
     * @since 0.3
     */
    public boolean removeSrcFile(String path) {
        if (path.startsWith("/"))
            path = path.substring(1);

        java.nio.file.Path relativePath = java.nio.file.Path.of(path);
        java.nio.file.Path relativeDirectoryPath = relativePath.getParent();
        String srcFileName = relativePath.getFileName().toString();
        SrcDirectory srcDirectory = this;

        if (srcFileName.length() == 0)
            return false;

        if (relativeDirectoryPath == null)
            return this.files.remove(srcFileName) != null;

        for (java.nio.file.Path currDirectoryPath : relativeDirectoryPath) {
            srcDirectory = srcDirectory.getDirectories().get(currDirectoryPath.toString());
            if (srcDirectory == null)
                return false;
        }

        return srcDirectory.getFiles().remove(srcFileName) != null;
    }

}
