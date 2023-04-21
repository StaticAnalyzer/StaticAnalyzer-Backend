package com.staticanalyzer.staticanalyzer.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.staticanalyzer.staticanalyzer.entity.analyse.DirectoryEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootTest
public class ArchiveTest {
    @Test
    void testTarGz() throws IOException {
        String fileName = "C:\\Users\\LambdaLe\\Desktop\\test\\test.tar.gz";
        File file = new File(fileName);

        FileInputStream fileInputStream = new FileInputStream(file);
        DirectoryEntry root = ArchiveUtils.decompress(fileInputStream.readAllBytes());

        System.out.println(root);
    }
}
