package br.com.infox.core.file.download;

class DocumentoInfo {
    private final String uid;
    private final String filename;

    DocumentoInfo(String uid, String filename) {
        this.uid = uid;
        this.filename = filename;
    }

    public String getUid() {
        return uid;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isFileNameEmpty() {
        return filename == null || filename.isEmpty();
    }

}