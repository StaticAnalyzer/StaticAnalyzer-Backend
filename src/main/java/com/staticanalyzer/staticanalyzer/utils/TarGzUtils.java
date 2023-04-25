package com.staticanalyzer.staticanalyzer.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysis;

/**
 * tar.gz工具类
 * 
 * @author WLLEGit
 * @version 0.0.1
 */
public class TarGzUtils {

    /**
     * 将项目压缩成项目包
     * 用于测试
     * 
     * @param projectPath 相对路径
     * @return 压缩包字节串，如果出现异常，返回{@code null}
     */
    public static byte[] compress(String projectPath) {
        /* todo */
        return null;
    }

    /**
     * 将项目包解压成文件集
     * 用于后续分析和结构化显示
     * 
     * @param tarGzFileBytes
     * @return 以相对路径为键值的文件映射，如果出现异常，返回{@code null}
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysis
     */
    public static Map<String, FileAnalysis> decompress(byte[] tarGzFileBytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(tarGzFileBytes);
        GZIPInputStream gzipInputStream;
        try {
            gzipInputStream = new GZIPInputStream(byteArrayInputStream);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }

        TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream);
        try {
            Map<String, FileAnalysis> files = new HashMap<>();
            TarArchiveEntry archiveEntry;
            while ((archiveEntry = tarArchiveInputStream.getNextTarEntry()) != null) {
                if (archiveEntry.isFile()) {
                    int archiveEntrySize = (int) archiveEntry.getSize();
                    byte[] content = new byte[archiveEntrySize];
                    tarArchiveInputStream.read(content, 0, archiveEntrySize);

                    FileAnalysis newFileEntry = new FileAnalysis();
                    String newFilePath = archiveEntry.getName();
                    newFileEntry.setName(Path.of(newFilePath).getFileName().toString());
                    newFileEntry.setSrc(new String(content));
                    files.put(newFilePath, newFileEntry);
                }
            }
            return files;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        } finally {
            try {
                tarArchiveInputStream.close();
            } catch (IOException ioException2) {
                ioException2.printStackTrace();
            }
        }
    }
}
