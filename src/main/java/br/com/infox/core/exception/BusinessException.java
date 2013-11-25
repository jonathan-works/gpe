package br.com.infox.core.exception;
import org.jboss.seam.annotations.ApplicationException;

@ApplicationException(rollback=false, end=false)
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BusinessException() {
		super();
	}
	
	public BusinessException(String cause) {
		super(cause);
	}
	
	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}
	
}