package br.com.infox.jwt;

import br.com.infox.core.exception.ErrorCode;

public enum JWTErrorCodes implements ErrorCode {
    INVALID_RSA_KEY(0x100), SIGNATURE_VERIFICATION_ERROR(0x101), UNSUPPORTED_ALGORITHM(
            0x201), FAILED_TO_PARSE_ALGORITHM(0x200), INVALID_CLAIM(0x300);

    private final int errorCode;

    private JWTErrorCodes(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

}
