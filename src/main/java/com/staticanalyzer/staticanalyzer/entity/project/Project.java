package com.staticanalyzer.staticanalyzer.entity.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import com.staticanalyzer.algservice.AnalyseResponse;

/**
 * 项目类
 * <p>
 * 项目上传时间格式为{@code yyyy-MM-dd HH:mm:ss}
 * </p>
 * 
 * @author YangYu
 * @since 0.1
 */
@lombok.Setter
@lombok.Getter
@lombok.NoArgsConstructor
@ApiModel(description = "项目信息")
public class Project {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "项目id", required = false)
    private Integer id;

    @ApiModelProperty(value = "所有者id", required = false)
    private int userId; // 此为所有者id，与id不同

    @TableField(value = "timestamp")
    @ApiModelProperty(value = "项目上传时间戳", required = false)
    private java.util.Date timestamp;

    @ApiModelProperty(value = "源码包", required = true)
    private byte[] sourceCode;

    @ApiModelProperty(value = "项目配置文件", required = true)
    private String config;

    @ApiModelProperty(value = "分析结果", required = false)
    private String analyseResult;

    /**
     * 接收分析结果
     * <p>
     * 使用protobuf自带的json序列化
     * </p>
     * 
     * @param analyseResponse
     * @return 异常时返回{@code false}
     * @see com.google.protobuf.util.JsonFormat
     */
    public boolean updateAnalyseResult(AnalyseResponse analyseResponse) {
        try {
            analyseResult = JsonFormat.printer()
                    .includingDefaultValueFields()
                    .omittingInsignificantWhitespace()
                    .print(analyseResponse);
            return true;
        } catch (InvalidProtocolBufferException invalidProtocolBufferException) {
            invalidProtocolBufferException.printStackTrace();
            return false;
        }
    }

    /**
     * 提取分析结果
     * <p>
     * 使用protobuf自带的json序列化
     * </p>
     * 
     * @return 异常时返回{@code null}
     * @see com.google.protobuf.util.JsonFormat
     */
    public AnalyseResponse resolveAnalyseResponse() {
        AnalyseResponse.Builder builder = AnalyseResponse.newBuilder();
        try {
            JsonFormat.parser().merge(analyseResult, builder);
            return builder.build();
        } catch (InvalidProtocolBufferException invalidProtocolBufferException) {
            invalidProtocolBufferException.printStackTrace();
            return null;
        }
    }

}
