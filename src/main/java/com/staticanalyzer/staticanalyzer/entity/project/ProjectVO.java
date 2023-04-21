package com.staticanalyzer.staticanalyzer.entity.project;

import java.util.List;

import com.google.type.Date;
import com.staticanalyzer.staticanalyzer.entity.analyse.AnalyseBrief;

import lombok.Data;

enum ProjectStatus {
    Complete,
    Queueing,
    Error
}

@Data
public class ProjectVO {
    private int id;
    private Date timestamp;
    private ProjectStatus status;
    private String config;
    private List<AnalyseBrief> analyseBrief;
}
