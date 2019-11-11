package br.com.infox.seam.exception;

import java.util.List;

import org.jboss.seam.annotations.ApplicationException;

@ApplicationException(rollback = false, end = false)
@javax.ejb.ApplicationException(rollback = false, inherited = false)
public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private List<String> validationMessages;
	
	public ValidationException() {
		super();
	}

    public ValidationException(String cause) {
        super(cause);
    }
    
    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ValidationException(List<String> validationMessages) {
    	super();
    	this.validationMessages = validationMessages;
    }
    
    public ValidationException(List<String> validationMessages, Throwable cause) {
    	super(cause);
    	this.validationMessages = validationMessages;
    }

	public List<String> getValidationMessages() {
		return validationMessages;
	}

	public void setValidationMessages(List<String> validationMessages) {
		this.validationMessages = validationMessages;
	}
    
}
