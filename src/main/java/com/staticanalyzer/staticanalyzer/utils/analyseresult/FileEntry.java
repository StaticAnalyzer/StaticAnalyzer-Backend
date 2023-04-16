package com.staticanalyzer.staticanalyzer.utils.analyseresult;

import lombok.Data;

import java.util.List;

import com.staticanalyzer.staticanalyzer.model.AnalyseResult;

@Data
public class FileEntry {
    private String name;
    private String path;
    private String content;
    private List<AnalyseResult> analyseResults;
}
