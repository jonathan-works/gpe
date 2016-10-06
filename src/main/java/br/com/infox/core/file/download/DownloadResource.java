package br.com.infox.core.file.download;

import java.io.IOException;
import java.io.InputStream;

interface DownloadResource {

    long getDataLength();

    String getFileName();

    String getContentType();

    void delete() throws IOException;

    InputStream getInputStream() throws IOException;

    void create() throws IOException;

    void handle(byte[] bytes, int length) throws IOException;

}