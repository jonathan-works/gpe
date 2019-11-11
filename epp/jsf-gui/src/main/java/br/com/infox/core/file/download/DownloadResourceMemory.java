package br.com.infox.core.file.download;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

class DownloadResourceMemory implements DownloadResource {

    byte[] bytes;
    private String contentType;
    private String fileName;

    public DownloadResourceMemory(String fileName, String contentType) {
        this.fileName = fileName;
        this.contentType = contentType;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public long getDataLength() {
        return this.bytes.length;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public void delete() throws IOException {
        bytes = null;
    }

    @Override
    public void create() throws IOException {
    }

    @Override
    public void handle(byte[] bytes, int length) throws IOException {
        this.bytes = Arrays.copyOf(bytes, length);
    }
}