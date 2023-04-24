package com.staticanalyzer.staticanalyzer.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

    public static byte[] compress(String projectPath) throws IOException {

    }

    /**
     * 将项目包解压成文件集
     * 用于后续分析和结构化显示
     * 
     * @param tarGzFileBytes
     * @return 以相对路径为键值的文件映射
     * @throws IOException
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysis
     */
    public static Map<String, FileAnalysis> decompress(byte[] tarGzFileBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(tarGzFileBytes);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
        TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream);

        Map<String, FileAnalysis> files = new HashMap<>();
        TarArchiveEntry archiveEntry;
        while ((archiveEntry = tarArchiveInputStream.getNextTarEntry()) != null) {
            if (archiveEntry.isFile()) {
                int archiveEntrySize = (int) archiveEntry.getSize();
                byte[] content = new byte[archiveEntrySize];
                tarArchiveInputStream.read(content, 0, archiveEntrySize);

                FileAnalysis newFileEntry = new FileAnalysis();
                newFileEntry.setName(archiveEntry.getName());
                newFileEntry.setSrc(new String(content));
                files.put(newFileEntry.getName(), newFileEntry);
            }
        }

        tarArchiveInputStream.close();
        return files;
    }
}
