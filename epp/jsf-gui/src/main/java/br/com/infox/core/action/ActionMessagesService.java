package br.com.infox.core.action;

import static java.text.MessageFormat.format;

import java.io.Serializable;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.GenericDatabaseErrorCode;
import br.com.infox.core.util.ExceptionUtil;

@Name(ActionMessagesService.NAME)
@AutoCreate
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ActionMessagesService implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "actionMessagesService";
    private static final String LOCK_MESSAGE = "entity.lock";
    
    public String handleException(final String msg, final Exception e){
        final StatusMessages messages = getMessagesHandler();
        messages.clearGlobalMessages();
        messages.clear();
        messages.add(Severity.ERROR, msg, e);
        return null;
    }
    
    public String handleBeanViolationException(final ConstraintViolationException e) {
        final StatusMessages messages = getMessagesHandler();
        messages.clearGlobalMessages();
        messages.clear();
        messages.add(getMessageForBeanViolationException(e));
        return null;
    }

    public String handleDAOException(final DAOException daoException) {
        final GenericDatabaseErrorCode errorCode = daoException.getDatabaseErrorCode();
        final StatusMessages messages = getMessagesHandler();
        String message = getMessageForDAOException(daoException);
        
        if (errorCode != null) {
        	messages.clearGlobalMessages();
        	messages.add(message);
            return errorCode.toString();
        } else {
            if (daoException.getMessage() != null) {
                messages.add(StatusMessage.Severity.ERROR, message, daoException);
            } else {
                final Throwable cause = daoException.getCause();
                if (cause instanceof ConstraintViolationException) {
                    return handleBeanViolationException((ConstraintViolationException) cause);
                } else {
                    messages.add(StatusMessage.Severity.ERROR, message, cause);
                }
            }
        }
        return null;
    }
    
    public String getMessageForDAOException(DAOException exception) {
        if (exception.getDatabaseErrorCode() != null) {
            return getInfoxMessages().get(exception.getLocalizedMessage());
        } else {
            String pattern = getInfoxMessages().get("entity.error.save");
            if (!pattern.contains("{")) {
            	pattern = "{0}";
            }
            Throwable cause = exception.getCause();
            if (cause instanceof ConstraintViolationException) {
                return getMessageForBeanViolationException((ConstraintViolationException) cause);
            } else if (exception.getMessage() != null) {
            	return format(pattern, exception.getMessage());
            } else {
                return format(pattern, cause.getMessage());
            }
        }
    }
    
    private String getMessageForBeanViolationException(ConstraintViolationException e) {
        StringBuilder sb = new StringBuilder();
    	for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            sb.append(format("{0}: {1}; ", violation.getPropertyPath(), violation.getMessage()));
        }
        return sb.toString();
    }

    private void handlePersistenceException(Exception exception) {
        handleDAOException(new DAOException(exception));
    }
    private StatusMessages getMessagesHandler() {
        return FacesMessages.instance();
    }
    
    public void handleGenericException(Exception exception) {
    	handleGenericException(exception, getInfoxMessages().get(LOCK_MESSAGE));
    }
    
    public void handleGenericException(Exception exception, String lockExceptionMessage) {
		if (ExceptionUtil.isLockException(exception)) {
			FacesMessages.instance().add(lockExceptionMessage);
		} else if (exception instanceof DAOException) {
			handleDAOException((DAOException) exception);
		} else if (exception instanceof EJBException) {
	        handleException(findRealExceptionMessage((Exception) exception), exception);
		} else if (exception instanceof PersistenceException) {
	        handlePersistenceException(exception);
		} else if (exception.getMessage() != null) {
			handleException(exception.getMessage(), exception);
		} else {
			handleException(exception.toString(), exception);
		}
	}
    
    private String findRealExceptionMessage(Exception exception) {
    	if (exception.getCause() == null) {
    		return exception.getMessage();
    	}
    	return findRealExceptionMessage((Exception) exception.getCause());
	}

	private InfoxMessages getInfoxMessages() {
    	return InfoxMessages.getInstance();
    }

}
