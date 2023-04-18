package com.staticanalyzer.staticanalyzer.entity.data;

import lombok.Data;

import java.util.List;

import com.staticanalyzer.staticanalyzer.entity.analysis.AnalyseResult;

@Data
public class SourceFile {
    private String name;
    private String path;
    private String content;
    private List<AnalyseResult> analyseResults;
}
