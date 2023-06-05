package com.staticanalyzer.staticanalyzer.entity.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@lombok.Setter
@lombok.Getter
@lombok.NoArgsConstructor
@ApiModel(description = "源文件实体类")
public class SrcFile {

    @ApiModelProperty(value = "文件名", required = true)
    protected String name;

    @ApiModelProperty(value = "源代码", required = false)
    protected String src;

}
