package com.staticanalyzer.staticanalyzer.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import com.staticanalyzer.staticanalyzer.entity.analyse.DirectoryEntry;
import com.staticanalyzer.staticanalyzer.entity.analyse.FileEntry;

public class ZipUtils {

    public static DirectoryEntry<FileEntry> fromTarGz(byte[] tarGzFileBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(tarGzFileBytes);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
        TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream);

        DirectoryEntry<FileEntry> rootEntry = new DirectoryEntry<>();
        TarArchiveEntry archiveEntry;
        while ((archiveEntry = tarArchiveInputStream.getNextTarEntry()) != null) {
            if (archiveEntry.isDirectory()) {
                rootEntry.createAbsolute(archiveEntry.getName());
            } else if (archiveEntry.isFile()) {
                int archiveEntrySize = (int) archiveEntry.getSize();
                byte[] content = new byte[archiveEntrySize];
                tarArchiveInputStream.read(content, 0, archiveEntrySize);

                FileEntry fileEntry = new FileEntry();
                fileEntry.setName(archiveEntry.getName());
                fileEntry.setSrc(new String(content));
                rootEntry.addFile(archiveEntry.getName(), fileEntry);
            }
        }

        tarArchiveInputStream.close();
        return rootEntry;
    }

    public static DirectoryEntry<FileEntry> fromTarXz(byte[] tarXzFileBytes) throws IOException {
        return null;
    }

    public static DirectoryEntry<FileEntry> fromRar(byte[] rarFileBytes) throws IOException {
        return null;
    }

    public static DirectoryEntry<FileEntry> from7z(byte[] _7zFileBytes) throws IOException {
        return null;
    }
}