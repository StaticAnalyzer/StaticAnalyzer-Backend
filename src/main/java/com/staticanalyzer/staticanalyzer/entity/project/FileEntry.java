package com.staticanalyzer.staticanalyzer.entity.project;

/**
 * 文件单元
 * 用于结构化显示项目
 * 
 * @author iu_oi
 * @version 0.0.1
 */
public interface FileEntry {

    /**
     * @return 对项目根目录的相对路径
     */
    public String getName();
}
