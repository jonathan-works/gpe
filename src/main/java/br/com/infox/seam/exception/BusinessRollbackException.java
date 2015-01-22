package br.com.infox.seam.exception;

public class BusinessRollbackException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BusinessRollbackException() {
    }

    public BusinessRollbackException(String message) {
        super(message);
    }
    
    public BusinessRollbackException(Throwable cause) {
        super(cause);
    }

    public BusinessRollbackException(String message, Throwable cause) {
        super(message, cause);
    }
}
