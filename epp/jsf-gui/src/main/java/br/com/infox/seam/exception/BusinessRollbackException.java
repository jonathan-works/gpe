package br.com.infox.seam.exception;

import org.jboss.seam.annotations.ApplicationException;

@ApplicationException(rollback = true, end = false)
@javax.ejb.ApplicationException(rollback = true)
public class BusinessRollbackException extends BusinessException {
    
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
