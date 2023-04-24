package com.staticanalyzer.staticanalyzer.entity.project;

import java.util.Date;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import com.staticanalyzer.algservice.AnalyseResponse;

/**
 * 项目信息
 */
@Data
@ApiModel(description = "项目信息")
public class Project {

    /**
     * 项目id
     * 由mysql自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "项目id", required = false)
    private int id;

    /**
     * 所有者id
     */
    @ApiModelProperty(value = "所有者id", required = false)
    private int userId;

    /**
     * 项目上传时间戳
     * 
     * 时间格式{@code yyyy-MM-dd HH:mm:ss}
     * 运行mysql的时区为标准时，此处设置时区为东8区
     */
    @TableField(value = "timestamp", fill = FieldFill.INSERT)
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "项目上传时间戳", required = false)
    private Date timestamp;

    /**
     * 源码包
     */
    @ApiModelProperty(value = "源码包", required = true)
    private byte[] sourceCode;

    /**
     * 项目配置文件
     */
    @ApiModelProperty(value = "项目配置文件", required = true)
    private String config;

    /**
     * 分析结果
     * json字串，由算法任务获取后填入
     * 
     * @see algservice.AnalyseResponse
     */
    @ApiModelProperty(value = "分析结果", required = false)
    private String analyseResult;

    /**
     * 接收分析结果
     * 
     * @apiNote 使用protobuf自带的json序列化
     * @param analyseResponse
     * @return 成功获取时返回{@code 真}，出现IOException返回{@code 假}
     * @see com.google.protobuf.util.JsonFormat
     */
    public boolean updateAnalyseResult(AnalyseResponse analyseResponse) {
        try {
            analyseResult = JsonFormat.printer()
                    .includingDefaultValueFields()
                    .omittingInsignificantWhitespace()
                    .print(analyseResponse);
        } catch (InvalidProtocolBufferException invalidProtocolBufferException) {
            return false;
        }
        return true;
    }

    /**
     * 提取分析结果
     * 
     * @apiNote 使用protobuf自带的json序列化
     * @return 解析失败时返回{@code null}
     * @see com.google.protobuf.util.JsonFormat
     */
    public AnalyseResponse resolveAnalyseResponse() {
        AnalyseResponse.Builder builder = AnalyseResponse.newBuilder();
        try {
            JsonFormat.parser().merge(analyseResult, builder);
        } catch (InvalidProtocolBufferException invalidProtocolBufferException) {
            return null;
        }
        return builder.build();
    }
}
