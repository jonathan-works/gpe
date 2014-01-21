package br.com.infox.core.exception;

public class ExcelExportException extends Exception {
    private static final long serialVersionUID = 1L;

    public ExcelExportException() {
    }

    public ExcelExportException(String message) {
        super(message);
    }

    public ExcelExportException(Throwable cause) {
        super(cause);
    }

    public ExcelExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
