package br.com.infox.security.rsa;

import br.com.infox.core.exception.ErrorCode;

public enum RSAErrorCodes implements ErrorCode {
    NO_SUCH_ALGORITHM(0x100), INVALID_PUBLIC_KEY_STRUCTURE(0x200),INVALID_PRIVATE_KEY_STRUCTURE(0x201)
    ;
    private RSAErrorCodes(int errorCode) {
        this.errorCode = errorCode;
    }

    private final int errorCode;

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

}
