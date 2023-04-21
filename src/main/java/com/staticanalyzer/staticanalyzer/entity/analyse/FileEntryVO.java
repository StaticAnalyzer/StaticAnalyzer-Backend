package com.staticanalyzer.staticanalyzer.entity.analyse;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

import com.staticanalyzer.algservice.AnalyseResultEntry;

@Data
@ApiModel(description = "单个文件分析简报")
public class FileEntryVO {

    @ApiModelProperty(value = "文件名", required = true)
    private String name;

    @ApiModelProperty(value = "最高级别分析结果", required = true)
    private AnalyseStatus severity;

    public static FileEntryVO fromFileEntry(FileEntry fileEntry) {
        FileEntryVO newFileEntryVO = new FileEntryVO();
        newFileEntryVO.setName(fileEntry.getName());
        newFileEntryVO.setSeverity(AnalyseStatus.Pass);

        for (AnalyseResultEntry analyseResult : fileEntry.getAnalyseResults()) {
            AnalyseStatus currentSeverity = AnalyseStatus.valueOf(analyseResult.getSeverity());
            if (currentSeverity.ordinal() > newFileEntryVO.getSeverity().ordinal())
                newFileEntryVO.setSeverity(currentSeverity);
        }

        return newFileEntryVO;
    }
}
