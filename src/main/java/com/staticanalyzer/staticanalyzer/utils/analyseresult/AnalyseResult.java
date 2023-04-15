package com.staticanalyzer.staticanalyzer.utils.analyseresult;

import lombok.Data;

@Data
public class AnalyseResult {
    private int startLine;
    private int endLine;
    private int startColumn;
    private int endColumn;
    private String message;
    private AnalyseSeverity severity;
}
