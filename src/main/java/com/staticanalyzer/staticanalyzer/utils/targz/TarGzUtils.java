package com.staticanalyzer.staticanalyzer.utils.targz;

import com.staticanalyzer.staticanalyzer.utils.analyseresult.DirectoryEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class TarGzUtils {
    public static DirectoryEntry decompress(byte[] fileBytes) throws IOException {
        DirectoryEntry root = new DirectoryEntry();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
        TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream);

        TarArchiveEntry entry;
        while ((entry = tarArchiveInputStream.getNextTarEntry()) != null) {
            if (entry.isFile()){
                byte[] content = new byte[(int) entry.getSize()];
                tarArchiveInputStream.read(content, 0, content.length);
                root.addFile("", entry.getName(), new String(content));
            }
            else if(entry.isDirectory()){
                root.addDirectory(entry.getName());
            }
        }
        return root;
    }
}
