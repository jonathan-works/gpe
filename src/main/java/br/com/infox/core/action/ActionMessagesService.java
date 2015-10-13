package br.com.infox.core.action;

import static java.text.MessageFormat.format;

import java.io.Serializable;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.OptimisticLockException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.GenericDatabaseErrorCode;

@Name(ActionMessagesService.NAME)
@AutoCreate
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ActionMessagesService implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "actionMessagesService";
    
    @In
    private InfoxMessages infoxMessages;

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
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            final String message = format("{0}: {1}", violation.getPropertyPath(), violation.getMessage());
            messages.add(message);
        }
        return null;
    }

    public String handleDAOException(final DAOException daoException) {
        final GenericDatabaseErrorCode errorCode = daoException.getDatabaseErrorCode();
        final StatusMessages messages = getMessagesHandler();
        if (errorCode != null) {
            final String ret = errorCode.toString();
            messages.clearGlobalMessages();
            messages.add(daoException.getLocalizedMessage());
            return ret;
        } else {
            String pattern = infoxMessages.get("entity.error.save");
            if (!pattern.contains("{")) {
            	pattern = "{0}";
            }
            if (daoException.getMessage() != null) {
                messages.add(StatusMessage.Severity.ERROR, format(pattern, daoException.getMessage()), daoException);
            } else {
                final Throwable cause = daoException.getCause();
                if (cause instanceof ConstraintViolationException) {
                    return handleBeanViolationException((ConstraintViolationException) cause);
                } else {
                    messages.add(StatusMessage.Severity.ERROR, format(pattern, cause.getMessage()), cause);
                }
            }
        }
        return null;
    }

    private StatusMessages getMessagesHandler() {
        return FacesMessages.instance();
    }
    
    public void handleLockException(Exception exception, String lockMessage) {
		if (isLockException(exception)) {
			FacesMessages.instance().add(lockMessage);
		} else if (exception instanceof DAOException) {
			handleDAOException((DAOException) exception);
		} else if (exception instanceof EJBException) {
			handleException(exception.getCause().getMessage(), exception);
		} else {
			handleException(exception.getMessage(), exception);
		}
	}
	
	private boolean isLockException(Exception exception) {
		return exception.getCause() instanceof OptimisticLockException || exception instanceof OptimisticLockException;
	}
}
