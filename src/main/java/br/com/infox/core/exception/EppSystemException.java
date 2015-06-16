package br.com.infox.core.exception;

import static java.text.MessageFormat.format;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import br.com.infox.core.messages.InfoxMessages;

public class EppSystemException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;
    private final Map<String, Object> parameters;

    public EppSystemException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
        this.parameters = new HashMap<>();
    }

    public EppSystemException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = new HashMap<>();
    }

    private EppSystemException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = new HashMap<>();
    }

    private EppSystemException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.parameters = new HashMap<>();
    }

    public static EppSystemException create(ErrorCode errorCode, String message, Throwable cause) {
        if (cause instanceof EppSystemException) {
            return (EppSystemException) cause;
        }
        return new EppSystemException(errorCode, message, cause);
    }

    public static EppSystemException create(ErrorCode errorCode, Throwable cause) {
        if (cause instanceof EppSystemException) {
            return (EppSystemException) cause;
        }
        return new EppSystemException(errorCode, cause);
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

    public String prettyMessage() {
        return InfoxMessages.getInstance().get(format("{0}_{1}", errorCode.getClass().getName(), errorCode.getErrorCode()));
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(errorCode.getClass().getName()).append(":").append(errorCode.getErrorCode());
        for (Entry<String, Object> entry : parameters.entrySet()) {
            sb.append("\n");
            sb.append(entry.getKey());
            sb.append(" : ");
            sb.append(entry.getValue());
        }
        return sb.toString();
    }

}
