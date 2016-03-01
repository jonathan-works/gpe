package br.com.infox.epp.processo.documento.bean;

import java.nio.charset.Charset;
import java.util.Base64;

import org.richfaces.model.UploadedFile;

public class DadosUpload {
    
    private String fileNameEncoded;
    private UploadedFile uploadedFile;
    private byte[] dadosArquivo;
    
    public DadosUpload(UploadedFile uploadedFile) {
        this(uploadedFile, null);
    }

    public DadosUpload(UploadedFile uploadedFile, byte[] dadosArquivo) {
        this.uploadedFile = uploadedFile;
        this.dadosArquivo = dadosArquivo;
        this.fileNameEncoded = Base64.getEncoder().encodeToString(uploadedFile.getName().getBytes(Charset.forName("UTF-8")));
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public byte[] getDadosArquivo() {
        return dadosArquivo;
    }
    
    public String getFileNameEncoded() {
        return fileNameEncoded;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fileNameEncoded == null) ? 0 : fileNameEncoded.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DadosUpload))
            return false;
        DadosUpload other = (DadosUpload) obj;
        if (fileNameEncoded == null) {
            if (other.fileNameEncoded != null)
                return false;
        } else if (!fileNameEncoded.equals(other.fileNameEncoded))
            return false;
        return true;
    }

}
