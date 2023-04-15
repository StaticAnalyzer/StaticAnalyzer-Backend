package com.staticanalyzer.staticanalyzer.utils.analyseresult;

import lombok.Data;

import java.util.List;

@Data
public class FileEntry {
    private String name;
    private String path;
    private String content;
    private List<AnalyseResult> analyseResults;
}
