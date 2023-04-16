package com.staticanalyzer.staticanalyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnalyseResult {
    private int startLine;
    private int endLine;
    private int startColumn;
    private int endColumn;
    private AnalyseSeverity severity;
    private String message;
}
