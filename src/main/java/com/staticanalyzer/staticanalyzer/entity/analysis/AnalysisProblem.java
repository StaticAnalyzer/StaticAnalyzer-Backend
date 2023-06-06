package com.staticanalyzer.staticanalyzer.entity.analysis;

@lombok.Getter
@lombok.Setter
public class AnalysisProblem {

    private String path;

    private AnalysisStatus severity;

    private int line;

    private String message;

}
