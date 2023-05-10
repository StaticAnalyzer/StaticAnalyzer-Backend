package com.staticanalyzer.staticanalyzer.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

public class TarGzFileCreator {

    private final ByteArrayOutputStream byteArrayOutputStream;

    private final TarArchiveOutputStream tarArchiveOutputStream;

    public TarGzFileCreator() throws IOException {
        byteArrayOutputStream = new ByteArrayOutputStream();
        tarArchiveOutputStream = new TarArchiveOutputStream(new GZIPOutputStream(byteArrayOutputStream));
    }

    public void addFileToTarGz(String fileName, String content) throws IOException {
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(fileName);
        tarArchiveEntry.setSize(content.length());
        tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);
        tarArchiveOutputStream.write(content.getBytes());
        tarArchiveOutputStream.closeArchiveEntry();
    }

    public byte[] getTarGzBytes() throws IOException {
        tarArchiveOutputStream.finish();
        tarArchiveOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
}
