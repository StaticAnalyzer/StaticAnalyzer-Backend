package com.staticanalyzer.staticanalyzer.algoService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.util.JsonFormat;
import com.staticanalyzer.algservice.AlgAnalyseResult;
import com.staticanalyzer.algservice.AnalyseResponse;
import com.staticanalyzer.algservice.AnalyseResultEntry;
import com.staticanalyzer.staticanalyzer.service.AlgorithmService;
import com.staticanalyzer.staticanalyzer.utils.TarGzFileCreator;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class AlgorithmServiceTests {
    @Autowired
    private AlgorithmService algorithmService;

    @Test
    public void TestConnection() throws IOException {
        TarGzFileCreator tarGzFileCreator = new TarGzFileCreator();
        tarGzFileCreator.addFileToTarGz("./test.cpp", "#include \"inc/inc.h\"\n#include \"testinclude/testinclude.h\"\n\n#include <iostream>\n\nint main()\n{\n    std::cout << inner(1) << std::endl;\n    std::cout << testinclude() << std::endl;\n    return 0;\n}");
        tarGzFileCreator.addFileToTarGz("./inc/inc.cpp", "#include \"inc.h\"\n\nint test(int x)\n{\n    return x;\n}\n\nint inner(int x)\n{\n    return test(x);\n}");
        tarGzFileCreator.addFileToTarGz("./inc/inc.h", "#pragma once\n\nint test(int x);\n\nint inner(int x);");
        tarGzFileCreator.addFileToTarGz("./include/testinclude/testinclude.h", "#pragma once\n\nint testinclude();");
        tarGzFileCreator.addFileToTarGz("./src/testinclude/testinclude.cpp", "#include \"testinclude/testinclude.h\"\n\nint testinclude()\n{\n    return 2;\n}");
        String config = "\nFramework\n{\n    queue_size = 100\n}\n                \nPrintLog\n{\n    level = 0\n    taintChecker = false\n    TemplateChecker = false\n    arrayBound = false\n    recursiveCall = false\n    divideChecker = false\n    memoryOPChecker = false\n}";

        String expect = "----------------------------------------------------------\nCFG of all functions:\n----------------------------------------------------------\nCFG of function test:\n---------------------\n\n [B2 (ENTRY)]\n   Succs (1): B1\n\n [B1]\n   1: return x;\n   Preds (1): B2\n   Succs (1): B0\n\n [B0 (EXIT)]\n   Preds (1): B1\n\nCFG of function inner:\n---------------------\n\n [B2 (ENTRY)]\n   Succs (1): B1\n\n [B1]\n   1: test(x)\n   2: return [B1.1];\n   Preds (1): B2\n   Succs (1): B0\n\n [B0 (EXIT)]\n   Preds (1): B1\n\nCFG of function testinclude:\n---------------------\n\n [B2 (ENTRY)]\n   Succs (1): B1\n\n [B1]\n   1: return 2;\n   Preds (1): B2\n   Succs (1): B0\n\n [B0 (EXIT)]\n   Preds (1): B1\n\nCFG of function main:\n---------------------\n\n [B2 (ENTRY)]\n   Succs (1): B1\n\n [B1]\n   1: inner(1)\n   2: std::cout << [B1.1] (OperatorCall)\n   3: [B1.2] << std::endl (OperatorCall)\n   4: testinclude()\n   5: std::cout << [B1.4] (OperatorCall)\n   6: [B1.5] << std::endl (OperatorCall)\n   7: return 0;\n   Preds (1): B2\n   Succs (1): B0\n\n [B0 (EXIT)]\n   Preds (1): B1\n\n----------------------------------------------------------\nCall Graph:\n----------------------------------------------------------\nCallee of main  . \n inner  int  \n testinclude  \nCallee of testinclude  . \nCallee of test  int  . \nCallee of inner  int  . \n test  int  \n----------------------------------------------------------\n";

        AnalyseResponse response = AnalyseResponse.newBuilder()
                .setCode(0)
                .setMsg("success")
                .addAlgAnalyseResults(
                        AlgAnalyseResult.newBuilder()
                                .setCode(2)
                                .setMsg("success")
                                .putFileAnalyseResults("test.cpp",
                                        AnalyseResultEntry.newBuilder().setMessage("message").build())
                                .setAnalyseType("AnalyseType").build())
                .build();

        String json = JsonFormat.printer().includingDefaultValueFields().print(response);
        System.out.println(json);

        AnalyseResponse.Builder builder = AnalyseResponse.newBuilder();
        JsonFormat.parser().merge(json, builder);
        AnalyseResponse response_regen = builder.build();


        List<AlgAnalyseResult> algAnalyseResultsList = response.getAlgAnalyseResultsList();
        AlgAnalyseResult algAnalyseResult = algAnalyseResultsList.get(1);
    }
}
