package br.com.infox.core.file.download;

public enum DocumentoServletOperation {
    DOWNLOAD("/download");

    private final String path;

    private DocumentoServletOperation(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
}