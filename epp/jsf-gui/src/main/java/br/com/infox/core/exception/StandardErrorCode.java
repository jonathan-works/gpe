package br.com.infox.core.exception;

public enum StandardErrorCode implements ErrorCode{
    UNKNOWN(0), CLONE(1)
    ;

    private final int errorCode;
    
    private StandardErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    
    @Override
    public int getErrorCode() {
        return errorCode;
    }

}
