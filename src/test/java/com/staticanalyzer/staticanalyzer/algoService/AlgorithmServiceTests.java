package com.staticanalyzer.staticanalyzer.algoService;

import com.staticanalyzer.staticanalyzer.algoservice.AlgorithmService;
import com.staticanalyzer.staticanalyzer.utils.TarGzFileCreator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
public class AlgorithmServiceTests {
    @Autowired
    private AlgorithmService algorithmService;

    @Test
    public void TestConnection() throws IOException {
        TarGzFileCreator tarGzFileCreator = new TarGzFileCreator();
        tarGzFileCreator.addFileToTarGz("main.cpp", "int main(){int a = 1;return 0;}");
        String config = """
                Framework
                {
                	queue_size = 100
                }
                                
                PrintLog
                {
                	level = 0
                	taintChecker = false
                	TemplateChecker = false
                	arrayBound = false
                	recursiveCall = false
                	divideChecker = false
                	memoryOPChecker = false
                }
                                
                """;

        System.out.println(algorithmService.JustReturn(tarGzFileCreator.getTarGzBytes(), config));
    }
}
