package br.com.infox.certificado.exception;

public class CertificadoException extends Exception {

    private static final long serialVersionUID = 1L;

    public CertificadoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CertificadoException(Throwable cause) {
        super(cause);
    }

    public CertificadoException(String message) {
        super(message);
    }
}
