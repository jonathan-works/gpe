package br.com.infox.core.exception;

import static java.text.MessageFormat.format;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import br.com.infox.core.messages.InfoxMessages;

public class SystemException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;
    private final Map<String, Object> parameters;

    SystemException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = new HashMap<>();
    }

    SystemException(ErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }

    SystemException(ErrorCode errorCode, Throwable cause) {
        this(errorCode, SystemException.getLocaleMessage(errorCode), cause);
    }

    SystemException(ErrorCode errorCode) {
        this(errorCode, null, null);
    }

    protected static String getLocaleMessage(ErrorCode errorCode, Object... arguments) {
        String messageKey = format("{0}_{1}", errorCode.getClass().getName(), errorCode.getErrorCode());
        return format(InfoxMessages.getInstance().get(messageKey), arguments);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public SystemException set(String field, Object value) {
        parameters.put(field, value);
        return this;
    }

    public Object get(String field) {
        return parameters.get(field);
    }

    private String getLogErrorMessage() {
        StringBuilder sb = new StringBuilder();
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
