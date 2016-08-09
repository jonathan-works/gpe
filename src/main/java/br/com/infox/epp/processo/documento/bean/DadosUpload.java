package br.com.infox.epp.processo.documento.bean;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.richfaces.model.UploadedFile;


public class DadosUpload {
	
	private static final Logger logger = Logger.getLogger(DadosUpload.class.getCanonicalName());
    
    private String fileNameEncoded;
    private UploadedFile uploadedFile;
    
    public DadosUpload(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
        this.fileNameEncoded = Base64.encodeBase64String(uploadedFile.getName().getBytes(Charset.forName("UTF-8")));
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public String getFileNameEncoded() {
        return fileNameEncoded;
    }
    
    public void delete() {
    	if (uploadedFile != null) {
    		try {
				uploadedFile.delete();
			} catch (IOException e) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
    	}
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
