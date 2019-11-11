package br.com.infox.epp.documento.rest;

public class DocumentoDownloadWrapper {

    private String contentType;
    private String fileName;
    private byte[] data;
    private boolean pdf;

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }

    public void setPdf(boolean pdf) {
        this.pdf = pdf;
    }
    
    public boolean isPdf() {
        return pdf;
    }

}
