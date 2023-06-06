package com.staticanalyzer.staticanalyzer.utils;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.InvalidProtocolBufferException;

import com.staticanalyzer.algservice.AlgAnalyseResult;
import com.staticanalyzer.algservice.AnalyseResponse;
import com.staticanalyzer.algservice.AnalyseResultEntry;
import com.staticanalyzer.algservice.FileAnalyseResults;

@SpringBootTest
public class JsonTest {

    //@Test
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

    //@Test
    void TestGrpcJsonDeserialize() throws InvalidProtocolBufferException {
        String json = "{\n" +
                      "    \"code\": 0,\n" +
                      "    \"msg\": \"分析完成\",\n" +
                      "    \"algAnalyseResults\": [\n" +
                      "        {\n" +
                      "            \"analyseType\": \"UninitializedVariable\",\n" +
                      "            \"code\": 0,\n" +
                      "            \"msg\": \"\",\n" +
                      "            \"fileAnalyseResults\": {\n" +
                      "                \"main.cpp\": {\n" +
                      "                    \"analyseResults\": [\n" +
                      "                        {\n" +
                      "                            \"startLine\": 1,\n" +
                      "                            \"startColumn\": 1,\n" +
                      "                            \"endLine\": 1,\n" +
                      "                            \"endColumn\": 2,\n" +
                      "                            \"severity\": \"Error\",\n" +
                      "                            \"message\": \"error message\"\n" +
                      "                        },\n" +
                      "                        {\n" +
                      "                            \"startLine\": 4,\n" +
                      "                            \"startColumn\": 2,\n" +
                      "                            \"endLine\": 4,\n" +
                      "                            \"endColumn\": 8,\n" +
                      "                            \"severity\": \"Warning\",\n" +
                      "                            \"message\": \"error message\"\n" +
                      "                        }\n" +
                      "                    ]\n" +
                      "                },\n" +
                      "                \"src/a.c\": {\n" +
                      "                    \"analyseResults\": [\n" +
                      "                        {\n" +
                      "                            \"startLine\": 1,\n" +
                      "                            \"startColumn\": 3,\n" +
                      "                            \"endLine\": 1,\n" +
                      "                            \"endColumn\": 5,\n" +
                      "                            \"severity\": \"Error\",\n" +
                      "                            \"message\": \"error message\"\n" +
                      "                        },\n" +
                      "                        {\n" +
                      "                            \"startLine\": 4,\n" +
                      "                            \"startColumn\": 2,\n" +
                      "                            \"endLine\": 4,\n" +
                      "                            \"endColumn\": 8,\n" +
                      "                            \"severity\": \"Info\",\n" +
                      "                            \"message\": \"error message\"\n" +
                      "                        }\n" +
                      "                    ]\n" +
                      "                }\n" +
                      "            }\n" +
                      "        }\n" +
                      "    ]\n" +
                      "}";

        AnalyseResponse.Builder builder = AnalyseResponse.newBuilder();
        JsonFormat.parser().merge(json, builder);
        AnalyseResponse response_regen = builder.build();

        System.out.println(response_regen.toString());
    }
}
