package com.staticanalyzer.staticanalyzer;

import java.io.BufferedOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.junit.Test;

public class Testt {

    @Test
    public void testDirTarGzip() throws IOException {
        // 被压缩打包的文件夹
        Path source = Paths.get(this.getClass().getResource("/test/root").getPath().substring(1));
        // 如果不是文件夹抛出异常
        if (!Files.isDirectory(source)) {
            throw new IOException("请指定一个文件夹");
        }

        // 压缩之后的输出文件名称
        String tarFileName = source.toString() + ".tar.gz";

        // OutputStream输出流、BufferedOutputStream缓冲输出流
        // GzipCompressorOutputStream是gzip压缩输出流
        // TarArchiveOutputStream打tar包输出流（包含gzip压缩输出流）
        try (OutputStream fOut = Files.newOutputStream(Paths.get(tarFileName));
                BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
                GZIPOutputStream gzOut = new GZIPOutputStream(buffOut);
                TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {
            // 遍历文件目录树
            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

                // 当成功访问到一个文件
                @Override
                public FileVisitResult visitFile(Path file,
                        BasicFileAttributes attributes) throws IOException {

                    // 判断当前遍历文件是不是符号链接(快捷方式)，不做打包压缩处理
                    if (attributes.isSymbolicLink()) {
                        return FileVisitResult.CONTINUE;
                    }

                    // 获取当前遍历文件名称
                    Path targetFile = source.relativize(file);

                    // 将该文件打包压缩
                    TarArchiveEntry tarEntry = new TarArchiveEntry(
                            file.toFile(), targetFile.toString());
                    tOut.putArchiveEntry(tarEntry);
                    Files.copy(file, tOut);
                    tOut.closeArchiveEntry();
                    // 继续下一个遍历文件处理
                    return FileVisitResult.CONTINUE;
                }

                // 当前遍历文件访问失败
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    System.err.printf("无法对该文件压缩打包为tar.gz : %s%n%s%n", file, exc);
                    return FileVisitResult.CONTINUE;
                }

            });
            // for循环完成之后，finish-tar包输出流
            tOut.finish();
        }
    }
}
