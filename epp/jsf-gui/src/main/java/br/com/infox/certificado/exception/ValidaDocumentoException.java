package br.com.infox.certificado.exception;

public class ValidaDocumentoException extends Exception {
    private static final long serialVersionUID = 1L;

    public ValidaDocumentoException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidaDocumentoException(String message) {
        super(message);
    }
}
