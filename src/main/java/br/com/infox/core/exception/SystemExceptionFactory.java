package br.com.infox.core.exception;

import java.util.UUID;

public class SystemExceptionFactory {

    private SystemExceptionFactory() {
    }

    public static SystemException create(ErrorCode errorCode) {
        return new SystemException(errorCode);
    }

    public static SystemException create(ErrorCode errorCode, Throwable cause) {
        if (cause instanceof SystemException) {
            return (SystemException) cause;
        }
        return new SystemException(errorCode, cause);
    }

    public static SystemException createWithProtocol(ErrorCode errorCode) {
        return new EppSystemException(errorCode);
    }

    public static SystemException createWithProtocol(ErrorCode errorCode, Throwable cause) {
        if (cause instanceof EppSystemException) {
            return (SystemException) cause;
        }
        return new EppSystemException(errorCode, cause, UUID.randomUUID());
    }

}
