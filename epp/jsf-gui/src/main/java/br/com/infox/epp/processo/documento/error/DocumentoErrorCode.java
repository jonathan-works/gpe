package br.com.infox.epp.processo.documento.error;

import br.com.infox.core.exception.ErrorCode;

public enum DocumentoErrorCode implements ErrorCode{
    INVALID_DOCUMENT_TYPE(1);

    private final int errorCode;

    private DocumentoErrorCode(int errorCode){
        this.errorCode = errorCode;
    }
    
    @Override
    public int getErrorCode() {
        return errorCode;
    }
    
}
