package com.staticanalyzer.staticanalyzer.entity.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnalyseBrief {
    private String analyseType;
    private AnalyseSeverity status;
}
