package com.staticanalyzer.staticanalyzer.entity.project;

import java.util.List;
import java.util.Date;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.staticanalyzer.algservice.AlgAnalyseResult;
import com.staticanalyzer.algservice.AnalyseResponse;
import com.staticanalyzer.algservice.AnalyseResultEntry;
import com.staticanalyzer.algservice.FileAnalyseResults;
import com.staticanalyzer.staticanalyzer.entity.analysis.Analysis;
import com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisStatus;

/**
 * 项目类的前端交互版本
 * 
 * @author iu_oi
 * @since 0.0.2
 */
@Data
@ApiModel(description = "项目类的前端交互版本")
public class ProjectVO {

    /* 项目id */
    @ApiModelProperty(value = "项目id", required = true)
    private int id;

    /* 项目上传时间戳 */
    @ApiModelProperty(value = "项目上传时间戳", required = true)
    private Date timestamp;

    /**
     * 项目分析状态
     * 
     * @see com.staticanalyzer.staticanalyzer.entity.project.ProjectStatus
     */
    @ApiModelProperty(value = "项目分析状态", example = "Complete", required = true)
    private ProjectStatus status;

    /* 项目配置文件 */
    @ApiModelProperty(value = "项目配置文件", required = true)
    private String config;

    /**
     * 分析结果简报列表
     * 依据算法分类的简报
     * 
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.Analysis
     */
    @ApiModelProperty(value = "分析结果简报列表", required = false)
    private List<Analysis> analyseBrief;

    /**
     * 从项目提取数据
     * 
     * @apiNote 如果项目没有分析结果，则不填入analyseBrief
     * @param project
     * @return projectVO
     */
    public ProjectVO(Project project) {
        id = project.getId();
        timestamp = project.getTimestamp();
        config = project.getConfig();

        if (project.getAnalyseResult() == null) {
            status = ProjectStatus.Queueing;
            return;
        }

        AnalyseResponse analyseResponse = project.resolveAnalyseResponse();
        if (analyseResponse == null || analyseResponse.getCode() == 1) {
            status = ProjectStatus.Error;
            return;
        }

        List<AlgAnalyseResult> algAnalyseResultList = analyseResponse.getAlgAnalyseResultsList();
        for (AlgAnalyseResult algAnalyseResult : algAnalyseResultList) {
            Analysis algAnalyseBrief = new Analysis();
            algAnalyseBrief.setAnalyseType(algAnalyseResult.getAnalyseType());

            AnalysisStatus severity = AnalysisStatus.Pass;
            if (algAnalyseResult.getCode() == 1) {
                severity = AnalysisStatus.AnalyseError;
            } else {
                for (FileAnalyseResults entry : algAnalyseResult.getFileAnalyseResultsMap().values()) {
                    for (AnalyseResultEntry analyseResultEntry : entry.getAnalyseResultsList()) {
                        AnalysisStatus currentSeverity = AnalysisStatus.valueOf(analyseResultEntry.getSeverity());
                        if (currentSeverity.compareTo(severity) > 0)
                            severity = currentSeverity;
                    }
                }
            }
            algAnalyseBrief.setStatus(severity);
        }
        status = ProjectStatus.Complete;
    }
}
