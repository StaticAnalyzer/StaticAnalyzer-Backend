package com.staticanalyzer.staticanalyzer.utils;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.staticanalyzer.staticanalyzer.entity.file.SrcFile;

// 本测试与Spring Boot无关
public class TarGzTest {

    public static byte[] readAllBytesFromResource(String resourcePath) throws java.io.IOException {
        java.io.File targetFile = new java.io.File(TarGzTest.class.getResource(resourcePath).getFile());
        byte[] targetBytes = new byte[(int) targetFile.length()];

        java.io.FileInputStream fileInputStream = new java.io.FileInputStream(targetFile);
        fileInputStream.read(targetBytes);
        fileInputStream.close();
        return targetBytes;
    }

    private static String TARGET_TARGZ_PATH = "/cpython.tar.gz";
    private static String TARGET_PATH = "/cpython";

    @org.junit.Test
    public void testDecompress() throws java.io.IOException {
        // 解压缩文件
        byte[] tarGzBytes = readAllBytesFromResource(TARGET_TARGZ_PATH);
        java.util.Map<String, SrcFile> extractedFiles = TarGzUtils.decompress(tarGzBytes);

        // 比较直接读取的目录和解压缩后的目录中所有的文件
        Path targetPath = Path.of(this.getClass().getResource(TARGET_PATH).getPath().substring(1));
        Files.walkFileTree(targetPath, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws java.io.IOException {
                Path relativeFilePath = targetPath.relativize(file);
                SrcFile extractedFile = extractedFiles.get(relativeFilePath.toString().replace("\\", "/"));

                assertNotNull(extractedFile); // 需要可以读到
                assertEquals(extractedFile.getName(), relativeFilePath.getFileName().toString()); // 需要文件名相等
                String content = new String(Files.readAllBytes(file));
                String src = extractedFile.getSrc();
                assertEquals(src, content); // 需要内容相等
                System.out.println("File " + extractedFile.getName() + " passed the test!");

                return FileVisitResult.CONTINUE;
            }

        });

        System.out.println("(De)Compression test passed!");
    }

    @org.junit.Test
    public void testCompress() throws java.io.IOException {
        // todo
    }

}
