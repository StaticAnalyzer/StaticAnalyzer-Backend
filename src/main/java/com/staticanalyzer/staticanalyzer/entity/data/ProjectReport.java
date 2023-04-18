package com.staticanalyzer.staticanalyzer.entity.data;

import com.staticanalyzer.staticanalyzer.entity.analysis.AnalyseBrief;

import lombok.Data;

@Data
public class ProjectReport {
    private int id;
    // timestamp??
    private ProjectStatus status;
    private String config;
    private AnalyseBrief analyseBrief;
}
