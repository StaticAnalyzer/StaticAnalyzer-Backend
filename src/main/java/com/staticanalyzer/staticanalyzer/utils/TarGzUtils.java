package com.staticanalyzer.staticanalyzer.utils;

import java.io.ByteArrayOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import com.staticanalyzer.staticanalyzer.entity.file.SrcFile;

/**
 * tar.gz工具类
 * <p>
 * 该工具类操纵字节流与文件集之间的转换
 * </p>
 * <p>
 * 字节流为tar.gz格式，文件集为相对路径与文件的键值对
 * </p>
 * 
 * @author YangYu
 * @since 0.2
 */
public class TarGzUtils {

    /**
     * 将文件集压缩成tar.gz项目包
     * <p>
     * {@code key}为文件相对根目录路径，{@code value.name}为单独文件名
     * </p>
     * 
     * @param files 文件集
     * @return {@code tarGzProjBytes} tar.gz格式的项目包
     * @throws java.io.IOException
     */
    public static byte[] compress(java.util.Map<String, SrcFile> files) throws java.io.IOException {
        java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
        java.util.zip.GZIPOutputStream gzipOutputStream = new java.util.zip.GZIPOutputStream(byteArrayOutputStream);

        TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(gzipOutputStream);
        TarArchiveEntry tarArchiveEntry;
        byte[] tarGzProjBytes;

        try (tarArchiveOutputStream) {
            for (java.util.Map.Entry<String, SrcFile> entry : files.entrySet()) {
                tarArchiveEntry = new TarArchiveEntry(entry.getKey());
                tarArchiveEntry.setSize(entry.getValue().getSrc().length());
                tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);
                tarArchiveOutputStream.write(entry.getValue().getSrc().getBytes());
                tarArchiveOutputStream.closeArchiveEntry();
            }
            tarArchiveOutputStream.finish();
        }

        tarGzProjBytes = byteArrayOutputStream.toByteArray();
        return tarGzProjBytes;

    }

    /**
     * 将单个源文件压缩成tar.gz项目包
     * <p>
     * {@code key}为文件相对根目录路径，{@code value.name}为单独文件名
     * </p>
     * 
     * @param srcFile 源文件
     * @return {@code tarGzProjBytes} tar.gz项目包
     * @throws java.io.IOException
     */
    public static byte[] compressSingle(SrcFile srcFile) throws java.io.IOException {
        java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
        java.util.zip.GZIPOutputStream gzipOutputStream = new java.util.zip.GZIPOutputStream(byteArrayOutputStream);

        TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(gzipOutputStream);
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(srcFile.getName());
        tarArchiveEntry.setSize(srcFile.getSrc().length());
        byte[] tarGzProjBytes;

        try (tarArchiveOutputStream) {
            tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);
            tarArchiveOutputStream.write(srcFile.getSrc().getBytes());
            tarArchiveOutputStream.closeArchiveEntry();
            tarArchiveOutputStream.finish();
        }

        tarGzProjBytes = byteArrayOutputStream.toByteArray();
        return tarGzProjBytes;
    }

    /**
     * 将tar.gz项目包解压成源文件集
     * <p>
     * {@code key}为文件相对根目录路径，{@code value.name}为单独文件名
     * </p>
     * 
     * @param tarGzProjBytes tar.gz项目包
     * @return {@code files} 源文件集
     * @throws java.io.IOException
     * @see SrcFile
     */
    public static java.util.Map<String, SrcFile> decompress(byte[] tarGzProjBytes) throws java.io.IOException {
        java.util.Map<String, SrcFile> files = new java.util.HashMap<>();
        java.io.ByteArrayInputStream byteArrayInputStream = new java.io.ByteArrayInputStream(tarGzProjBytes);
        java.util.zip.GZIPInputStream gzipInputStream = new java.util.zip.GZIPInputStream(byteArrayInputStream);

        TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream);
        TarArchiveEntry tarArchiveEntry;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        SrcFile srcFile;

        try (tarArchiveInputStream) {
            int byteCount;
            byte[] byteBuffer = new byte[8192];
            while ((tarArchiveEntry = tarArchiveInputStream.getNextTarEntry()) != null) {
                if (tarArchiveEntry.isFile()) {
                    byteArrayOutputStream.reset();
                    byteCount = tarArchiveInputStream.read(byteBuffer, 0, byteBuffer.length);
                    while (byteCount != -1) {
                        byteArrayOutputStream.write(byteBuffer, 0, byteCount);
                        byteCount = tarArchiveInputStream.read(byteBuffer, 0, byteBuffer.length);
                    }

                    String tarArchiveName = tarArchiveEntry.getName();
                    srcFile = new SrcFile();
                    srcFile.setName(java.nio.file.Path.of(tarArchiveName).getFileName().toString());
                    srcFile.setSrc(new String(byteArrayOutputStream.toByteArray()));
                    files.put(tarArchiveName, srcFile);
                }
            }
        }

        return files;
    }

}
