package br.com.infox.core.exception;

import static java.text.MessageFormat.format;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import br.com.infox.core.messages.InfoxMessages;

public class EppSystemException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;
    private final Map<String, Object> parameters;
    private final String protocol;
    
    public EppSystemException(ErrorCode errorCode) {
        this(errorCode, null, UUID.randomUUID());
    }

    private EppSystemException(ErrorCode errorCode, Throwable cause, UUID protocol) {
        super(getLocaleMessage(errorCode, protocol), cause);
        this.errorCode = errorCode;
        this.parameters = new HashMap<>();
        this.protocol = protocol.toString();
    }

    private static String getLocaleMessage(ErrorCode errorCode, UUID protocol) {
        String messageKey = format("{0}_{1}", errorCode.getClass().getName(), errorCode.getErrorCode());
        return format(InfoxMessages.getInstance().get(messageKey), protocol.toString());
    }

    public static EppSystemException create(ErrorCode errorCode, Throwable cause) {
        if (cause instanceof EppSystemException) {
            return (EppSystemException) cause;
        }
        return new EppSystemException(errorCode, cause, UUID.randomUUID());
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public EppSystemException set(String field, Object value) {
        parameters.put(field, value);
        return this;
    }

    public Object get(String field) {
        return parameters.get(field);
    }
    
    private String getLogErrorMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\nProtocol ").append(protocol).append("\r\n");
        sb.append(errorCode.getClass().getName()).append(": ").append(errorCode.getErrorCode()).append("\r\n");
        sb.append("Parameters");
        for (Entry<String, Object> entry : parameters.entrySet()) {
            sb.append("   ").append(entry.getKey()).append(" : ").append(entry.getValue()).append("\r\n");
        }
        return sb.toString();
    }
     
    @Override
    public String toString() {
        return getLogErrorMessage();
    }
    
}
