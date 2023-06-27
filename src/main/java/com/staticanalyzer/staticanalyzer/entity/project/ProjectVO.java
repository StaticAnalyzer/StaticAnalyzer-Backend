package com.staticanalyzer.staticanalyzer.entity.project;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.staticanalyzer.algservice.AlgAnalyseResult;
import com.staticanalyzer.algservice.AnalyseResponse;
import com.staticanalyzer.algservice.AnalyseResultEntry;
import com.staticanalyzer.algservice.FileAnalyseResults;
import com.staticanalyzer.staticanalyzer.entity.analysis.Analysis;
import com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisStatus;

/**
 * 项目可视类
 * <p>
 * 项目上传时间格式为{@code yyyy-MM-dd HH:mm:ss}
 * 时区为东八区
 * </p>
 * <p>
 * 如果对应项目没有分析结果，则{@code analyseBrief}为{@code null}
 * </p>
 * 
 * @see ProjectStatus
 * @author YangYu
 * @since 0.2
 */
@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@ApiModel(description = "项目类的前端交互版本")
public class ProjectVO {

    @ApiModelProperty(value = "项目id", required = true)
    private int id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "项目上传时间戳", required = true)
    private Date timestamp;

    @ApiModelProperty(value = "项目分析状态", example = "Complete", required = true)
    private ProjectStatus status;

    @ApiModelProperty(value = "项目配置文件", required = true)
    private String config;

    @ApiModelProperty(value = "分析结果简报列表", required = false)
    private List<Analysis> analyseBrief;

    public ProjectVO(Project project) {
        id = project.getId();
        timestamp = project.getTimestamp();
        config = project.getConfig();
        analyseBrief = new LinkedList<>();

        // 正在等待中
        if (project.getAnalyseResult() == null) {
            status = ProjectStatus.Queueing;
            return;
        }

        // 出现分析错误
        AnalyseResponse analyseResponse = project.resolveAnalyseResponse();
        if (analyseResponse == null || analyseResponse.getCode() != 0) {
            status = ProjectStatus.Error;
            return;
        }

        // 分析完成，需要生成简报
        List<AlgAnalyseResult> algAnalyseResultList = analyseResponse.getAlgAnalyseResultsList();
        for (AlgAnalyseResult algAnalyseResult : algAnalyseResultList) {
            Analysis algAnalyseBrief = new Analysis();
            algAnalyseBrief.setAnalyseType(algAnalyseResult.getAnalyseType());

            AnalysisStatus severity = AnalysisStatus.Pass;
            if (algAnalyseResult.getCode() != 0) {
                severity = AnalysisStatus.AnalyseError;
            } else {
                for (FileAnalyseResults entry : algAnalyseResult.getFileAnalyseResultsMap().values()) {
                    for (AnalyseResultEntry analyseResultEntry : entry.getAnalyseResultsList()) {
                        AnalysisStatus currentSeverity = AnalysisStatus.valueOf(analyseResultEntry.getSeverity());
                        // 取最严重的简报
                        if (currentSeverity.compareTo(severity) > 0)
                            severity = currentSeverity;
                    }
                }
            }
            algAnalyseBrief.setStatus(severity);
            analyseBrief.add(algAnalyseBrief);
        }
        status = ProjectStatus.Complete;
    }

}
