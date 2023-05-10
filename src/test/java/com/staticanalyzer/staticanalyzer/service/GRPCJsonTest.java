package com.staticanalyzer.staticanalyzer.service;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.InvalidProtocolBufferException;

import com.staticanalyzer.algservice.AlgAnalyseResult;
import com.staticanalyzer.algservice.AnalyseResponse;
import com.staticanalyzer.algservice.AnalyseResultEntry;
import com.staticanalyzer.algservice.FileAnalyseResults;

@SpringBootTest
public class GRPCJsonTest {

    @Test
    void TestGrpcJsonSerialization() throws InvalidProtocolBufferException {
        AnalyseResponse.Builder builder = AnalyseResponse.newBuilder();
        builder.setCode(0);
        builder.setMsg("success");

        AlgAnalyseResult.Builder algResultBuilder = AlgAnalyseResult.newBuilder();
        algResultBuilder.setAnalyseType("UninitializedVariable");
        algResultBuilder.setCode(0);
        algResultBuilder.setMsg("success");

        FileAnalyseResults.Builder fileResultBuilder = FileAnalyseResults.newBuilder();
        AnalyseResultEntry.Builder entryBuilder = AnalyseResultEntry.newBuilder();
        entryBuilder.setStartLine(1);
        entryBuilder.setEndLine(1);
        entryBuilder.setStartColumn(1);
        entryBuilder.setEndColumn(3);
        entryBuilder.setMessage("Uninitialized variable: a");
        entryBuilder.setSeverity("error");
        fileResultBuilder.addAnalyseResults(entryBuilder.build());

        algResultBuilder.putFileAnalyseResults("main.cpp", fileResultBuilder.build());
        builder.addAlgAnalyseResults(algResultBuilder.build());

        String json = JsonFormat.printer().includingDefaultValueFields().print(builder.build());
        System.out.println(json);
    }

    @Test
    void TestGrpcJsonDeserialize() throws InvalidProtocolBufferException {
        String json = """
                {
                    "code": 0,
                    "msg": "分析完成",
                    "algAnalyseResults": [
                        {
                            "analyseType": "UninitializedVariable",
                            "code": 0,
                            "msg": "",
                            "fileAnalyseResults": {
                                "main.cpp": {
                                    "analyseResults": [
                                        {
                                            "startLine": 1,
                                            "startColumn": 1,
                                            "endLine": 1,
                                            "endColumn": 2,
                                            "severity": "Error",
                                            "message": "error message"
                                        },
                                        {
                                            "startLine": 4,
                                            "startColumn": 2,
                                            "endLine": 4,
                                            "endColumn": 8,
                                            "severity": "Warning",
                                            "message": "error message"
                                        }
                                    ]
                                },
                                "src/a.c": {
                                    "analyseResults": [
                                        {
                                            "startLine": 1,
                                            "startColumn": 3,
                                            "endLine": 1,
                                            "endColumn": 5,
                                            "severity": "Error",
                                            "message": "error message"
                                        },
                                        {
                                            "startLine": 4,
                                            "startColumn": 2,
                                            "endLine": 4,
                                            "endColumn": 8,
                                            "severity": "Info",
                                            "message": "error message"
                                        }
                                    ]
                                }
                            }
                        }
                    ]
                }
        """;

        AnalyseResponse.Builder builder = AnalyseResponse.newBuilder();
        JsonFormat.parser().merge(json, builder);
        AnalyseResponse response_regen = builder.build();

        System.out.println(response_regen.toString());
    }
}
