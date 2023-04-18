package com.staticanalyzer.staticanalyzer.entity.analysis;

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
