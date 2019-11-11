package br.com.infox.core.net;

import br.com.infox.core.exception.ErrorCode;

public enum URLErrorCode implements ErrorCode {
    URL_SYNTAX(0x100);
    private URLErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    private final int errorCode;

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }
}
