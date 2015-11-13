package br.com.infox.epp.processo.documento.bean;

import org.richfaces.model.UploadedFile;

public class DadosUpload {
    private UploadedFile uploadedFile;
    private byte[] dadosArquivo;

    public DadosUpload(UploadedFile uploadedFile, byte[] dadosArquivo) {
        super();
        this.uploadedFile = uploadedFile;
        this.dadosArquivo = dadosArquivo;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public byte[] getDadosArquivo() {
        return dadosArquivo;
    }
}
