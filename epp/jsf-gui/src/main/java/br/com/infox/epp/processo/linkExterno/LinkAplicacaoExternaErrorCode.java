package br.com.infox.epp.processo.linkExterno;

import br.com.infox.core.exception.ErrorCode;

public enum LinkAplicacaoExternaErrorCode implements ErrorCode {
    INVALID_PRIVATE_KEY(0x100)
    ;
    
    private LinkAplicacaoExternaErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    private final int errorCode;
    
    
    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

}
