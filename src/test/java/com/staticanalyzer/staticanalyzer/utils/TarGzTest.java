package com.staticanalyzer.staticanalyzer.utils;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.staticanalyzer.staticanalyzer.entity.file.SrcFile;

// 本测试与Spring Boot无关
public class TarGzTest {

    public static byte[] readAllBytesFromResource(String resourcePath) throws java.io.IOException {
        java.net.URL resourceURL = TarGzTest.class.getResource("/" + resourcePath);
        java.io.File targetFile = new java.io.File(resourceURL.getFile());
        byte[] targetBytes = new byte[(int) targetFile.length()];

        java.io.FileInputStream fileInputStream = new java.io.FileInputStream(targetFile);
        fileInputStream.read(targetBytes);
        fileInputStream.close();
        return targetBytes;
    }

    private static String TARGET_TARGZ_PATH = "cpython.tar.gz";
    private static String TARGET_PATH = "cpython";

    @org.junit.Test
    public void testDecompress() throws java.io.IOException {
        // 解压缩文件
        byte[] tarGzBytes = readAllBytesFromResource(TARGET_TARGZ_PATH);
        java.util.Map<String, SrcFile> extractedFiles = TarGzUtils.decompress(tarGzBytes);

        // 比较直接读取的目录和解压缩后的目录中所有的文件
        Path targetPath = Path.of(this.getClass().getResource("/" + TARGET_PATH).getPath().substring(1));
        Files.walkFileTree(targetPath, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws java.io.IOException {
                String fileRelative = targetPath.relativize(file).toString().replace("\\", "/");
                SrcFile extractedFile = extractedFiles.get(fileRelative);

                assertNotNull(extractedFile); // 需要可以读到
                assertEquals(extractedFile.getName(), file.getFileName().toString()); // 需要文件名相等
                assertEquals(extractedFile.getSrc(), new String(Files.readAllBytes(file))); // 需要内容相等

                System.out.println(fileRelative + " passed decompression test!");
                return FileVisitResult.CONTINUE;
            }

        });

        System.out.println("Decompression test passed!");
    }

    private static String TARGET_SINGLE_PATH = "cpython/Python/ast.c";

    @org.junit.Test
    public void testCompressSingle() throws java.io.IOException {
        byte[] srcFileBytes = readAllBytesFromResource(TARGET_SINGLE_PATH);

        SrcFile srcFile = new SrcFile();
        srcFile.setName(Path.of(TARGET_SINGLE_PATH).getFileName().toString());
        srcFile.setSrc(new String(srcFileBytes));

        java.util.Map<String, SrcFile> singleFileMap = TarGzUtils.decompress(TarGzUtils.compressSingle(srcFile));
        assertEquals(singleFileMap.get(srcFile.getName()).getName(), srcFile.getName());
        assertEquals(singleFileMap.get(srcFile.getName()).getSrc(), srcFile.getSrc());
        System.out.println(srcFile.getName() + " passed decompression test!");

        System.out.println("Single compression test passed!");
    }

}
