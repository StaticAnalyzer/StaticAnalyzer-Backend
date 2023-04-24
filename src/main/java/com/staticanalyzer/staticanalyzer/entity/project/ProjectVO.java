package com.staticanalyzer.staticanalyzer.entity.project;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.Date;

import lombok.Data;

import com.staticanalyzer.algservice.AlgAnalyseResult;
import com.staticanalyzer.algservice.AnalyseResponse;
import com.staticanalyzer.algservice.AnalyseResultEntry;
import com.staticanalyzer.algservice.FileAnalyseResults;
import com.staticanalyzer.staticanalyzer.entity.analysis.Analysis;
import com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisStatus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

enum ProjectStatus {
    Complete,
    Queueing,
    Error
}

@Data
@ApiModel(description = "项目分析简报")
public class ProjectVO {

    @ApiModelProperty(value = "项目id", required = true)
    private int id;

    @ApiModelProperty(value = "项目上传时间", required = true)
    private Date timestamp;

    @ApiModelProperty(value = "项目分析状态", required = true)
    private ProjectStatus status;

    @ApiModelProperty(value = "项目配置", required = true)
    private String config;

    @ApiModelProperty(value = "分析结果简报列表", required = false)
    private List<Analysis> analyseBrief;

    public static ProjectVO fromProject(Project project) {
        /* todo set timestamp */
        ProjectVO projectVO = new ProjectVO();
        projectVO.setId(project.getId());
        projectVO.setConfig(project.getConfig());
        if (project.getAnalyseResult() == null) {
            projectVO.setStatus(ProjectStatus.Queueing);
        } else {
            try {
                AnalyseResponse analyseResponse = project.parseAnalyseResponse();
                if (analyseResponse.getCode() == 1) {
                    projectVO.setStatus(ProjectStatus.Error);
                } else {
                    List<AlgAnalyseResult> algAnalyseResultList = analyseResponse.getAlgAnalyseResultsList();
                    for (AlgAnalyseResult algAnalyseResult : algAnalyseResultList) {
                        Analysis algAnalyseBrief = new Analysis();
                        algAnalyseBrief.setAnalyseType(algAnalyseResult.getAnalyseType());
                        AnalysisStatus severity = AnalysisStatus.Pass;
                        if (algAnalyseResult.getCode() == 1) {
                            severity = AnalysisStatus.AnalyseError;
                        } else {
                            Map<String, FileAnalyseResults> fileAnalyseResultMap = algAnalyseResult
                                    .getFileAnalyseResultsMap();
                            for (Map.Entry<String, FileAnalyseResults> entry : fileAnalyseResultMap.entrySet()) {
                                List<AnalyseResultEntry> analyseResultEntryList = entry.getValue()
                                        .getAnalyseResultsList();
                                for (AnalyseResultEntry analyseResultEntry : analyseResultEntryList) {
                                    AnalysisStatus currentSeverity = AnalysisStatus
                                            .valueOf(analyseResultEntry.getSeverity());
                                    if (currentSeverity.compareTo(severity) > 0)
                                        severity = currentSeverity;
                                }
                            }
                        }
                        algAnalyseBrief.setStatus(severity);
                    }
                }
            } catch (IOException ioException) {
                projectVO.setStatus(ProjectStatus.Error);
            }
        }
        return projectVO;
    }
}
