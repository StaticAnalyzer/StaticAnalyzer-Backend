package com.staticanalyzer.staticanalyzer.entity.analyse;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.staticanalyzer.algservice.AlgAnalyseResult;
import com.staticanalyzer.algservice.AnalyseResponse;
import com.staticanalyzer.algservice.AnalyseResultEntry;
import com.staticanalyzer.algservice.FileAnalyseResults;
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysis;

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

    public static boolean analyse(DirectoryEntry<FileAnalysis> root, AnalyseResponse analyseResponse) {
        Map<String, List<AnalyseResultEntry>> analyseMap = new HashMap<>();

        if (analyseResponse.getCode() == 1)
            return false;

        List<AlgAnalyseResult> algAnalyseResultList = analyseResponse.getAlgAnalyseResultsList();
        for (AlgAnalyseResult algAnalyseResult : algAnalyseResultList) {
            Map<String, FileAnalyseResults> fileAnalyseResultMap = algAnalyseResult.getFileAnalyseResultsMap();
            for (Map.Entry<String, FileAnalyseResults> entry : fileAnalyseResultMap.entrySet()) {
                List<AnalyseResultEntry> analyseResultEntryList = analyseMap.get(entry.getKey());
                if (analyseResultEntryList == null) {
                    analyseResultEntryList = new LinkedList<>();
                    analyseMap.put(entry.getKey(), analyseResultEntryList);
                }
                analyseResultEntryList.addAll(entry.getValue().getAnalyseResultsList());
            }
        }

        for (Map.Entry<String, List<AnalyseResultEntry>> entry : analyseMap.entrySet()) {
            FileAnalysis fileEntry = root.getFileAt(entry.getKey());
            fileEntry.setAnalyseResults(entry.getValue());
        }
        return true;
    }

    public static DirectoryEntry<FileEntryVO> visualize(DirectoryEntry<FileAnalysis> root) {
        DirectoryEntry<FileEntryVO> rootVO = new DirectoryEntry<>();

        Map<String, DirectoryEntry<FileEntryVO>> directoryEntryVOs = new HashMap<>();
        for (Map.Entry<String, DirectoryEntry<FileAnalysis>> entry : root.getDirectories().entrySet())
            directoryEntryVOs.put(entry.getKey(), visualize(entry.getValue()));

        Map<String, FileEntryVO> fileEntryVOs = new HashMap<>();
        for (Map.Entry<String, FileAnalysis> entry : root.getFiles().entrySet())
            fileEntryVOs.put(entry.getKey(), FileEntryVO.fromFileEntry(entry.getValue()));

        rootVO.setName(root.getName());
        rootVO.setDirectories(directoryEntryVOs);
        rootVO.setFiles(fileEntryVOs);
        return rootVO;
    }
}
