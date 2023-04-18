package com.staticanalyzer.staticanalyzer.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import org.springframework.stereotype.Component;

import com.staticanalyzer.staticanalyzer.entity.data.SourceDirectory;

@Component
public class TarGzUtils {

    public SourceDirectory decompress(byte[] fileBytes) throws IOException {
        TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(
                new GZIPInputStream(new ByteArrayInputStream(fileBytes)));

        SourceDirectory root = new SourceDirectory();
        TarArchiveEntry entry = tarArchiveInputStream.getNextTarEntry();
        while (entry != null) {
            if (entry.isFile()) {
                byte[] content = new byte[(int) entry.getSize()];
                tarArchiveInputStream.read(content, 0, content.length);
                root.addFile(entry.getName(), new String(content));
            } else if (entry.isDirectory()) {
                root.addDirectory(entry.getName());
            }
            entry = tarArchiveInputStream.getNextTarEntry();
        }

        tarArchiveInputStream.close();
        return root;
    }
}
