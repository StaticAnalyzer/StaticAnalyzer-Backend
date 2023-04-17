package com.staticanalyzer.staticanalyzer.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import com.staticanalyzer.staticanalyzer.model.analyseresult.DirectoryEntry;

public class TarGzUtils {

    public static DirectoryEntry decompress(byte[] fileBytes) throws IOException {
        DirectoryEntry root = new DirectoryEntry();
        TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(
                new GZIPInputStream(new ByteArrayInputStream(fileBytes)));
        TarArchiveEntry entry = tarArchiveInputStream.getNextTarEntry();

        while (entry != null) {
            if (entry.isFile()) {
                byte[] content = new byte[(int) entry.getSize()];
                tarArchiveInputStream.read(content, 0, content.length);
                root.addFile("", entry.getName(), new String(content));
            } else if (entry.isDirectory()) {
                root.addDirectory(entry.getName());
            }
            entry = tarArchiveInputStream.getNextTarEntry();
        }

        tarArchiveInputStream.close();
        return root;
    }
}
