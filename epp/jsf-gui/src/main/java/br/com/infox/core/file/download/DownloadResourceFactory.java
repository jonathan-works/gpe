package br.com.infox.core.file.download;

import java.io.IOException;

import javax.ejb.Stateless;

@Stateless
public class DownloadResourceFactory {

    public DownloadResource create(String fileName, String contentType, byte[] data) throws IOException {
        return create(false, fileName, contentType, data);
    }

    public DownloadResource create(boolean memory, String fileName, String contentType, byte[] data)
            throws IOException {
        if (memory)
            return createMem(fileName, contentType, data);
        else
            return createDisc(fileName, contentType, data);
    }

    private DownloadResource createMem(String fileName, String contentType, byte[] data) throws IOException {
        DownloadResourceMemory downloadResource = new DownloadResourceMemory(fileName, contentType);
        downloadResource.create();
        downloadResource.handle(data, data.length);
        return downloadResource;
    }

    private DownloadResource createDisc(String fileName, String contentType, byte[] data) throws IOException {
        DownloadResourceDisc downloadResource = new DownloadResourceDisc(fileName, contentType);
        downloadResource.create();
        downloadResource.handle(data, data.length);
        return downloadResource;
    }

}