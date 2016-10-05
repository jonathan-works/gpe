package br.com.infox.core.file.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import br.com.infox.epp.cdi.config.BeanManager;

class DownloadResourceDisc implements DownloadResource {

    private File file;
    private String fileName;
    private String contentType;

    public DownloadResourceDisc(String fileName, String contentType) {
        this.fileName = fileName;
        this.contentType = contentType;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public long getDataLength() {
        return file.length();
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void delete() throws IOException {
        if (file == null || !file.exists()) {
            return;
        }
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            throw new IOException("File delete failed");
        }
    }

    @Override
    public void create() throws IOException {
        File tempFolder = null;
        {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletRequest request = null;
            if (facesContext != null){
                request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
            } else {
                request = BeanManager.INSTANCE.getReference(HttpServletRequest.class);
            }
            tempFolder = (File)request.getServletContext().getAttribute(ServletContext.TEMPDIR);
        }
        file = File.createTempFile("downloadable_file", null, tempFolder);
        file.deleteOnExit();
    }

    @Override
    public void handle(byte[] bytes, int length) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)){
            fos.write(bytes, 0, length);
            fos.close();
        }
    }

}