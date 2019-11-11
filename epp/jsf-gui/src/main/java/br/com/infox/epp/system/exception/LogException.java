package br.com.infox.epp.system.exception;

public class LogException extends Exception {

    private static final long serialVersionUID = 1L;

    public LogException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogException(String message) {
        super(message);
    }

    public LogException(Throwable cause) {
        super(cause);
    }

}
