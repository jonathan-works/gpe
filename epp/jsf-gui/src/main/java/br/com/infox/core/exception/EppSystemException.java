package br.com.infox.core.exception;

import java.util.UUID;

public class EppSystemException extends SystemException {

    private static final long serialVersionUID = 1L;

    private final String protocol;

    public EppSystemException(ErrorCode errorCode) {
        this(errorCode, null, UUID.randomUUID());
    }

    EppSystemException(ErrorCode errorCode, Throwable cause, UUID protocol) {
        super(errorCode, SystemException.getLocaleMessage(errorCode, protocol.toString()), cause);
        this.protocol = protocol.toString();
    }

    @Deprecated
    public static EppSystemException create(ErrorCode errorCode, Throwable cause) {
        if (cause instanceof EppSystemException) {
            return (EppSystemException) cause;
        }
        return new EppSystemException(errorCode, cause, UUID.randomUUID());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\nProtocol ").append(protocol).append("\r\n");
        sb.append(super.toString());
        return sb.toString();
    }

}
