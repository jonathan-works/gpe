package br.com.infox.core.action;

import static java.text.MessageFormat.format;

import java.io.Serializable;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessages;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.hibernate.postgres.error.PostgreSQLErrorCode;

@Name(ActionMessagesService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class ActionMessagesService implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "actionMessagesService";

	public String handleBeanViolationException(final ConstraintViolationException e) {
        final StatusMessages messages = getMessagesHandler();
        messages.clear();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            final String message = format("{0}: {1}", violation.getPropertyPath(), violation.getMessage());
            messages.add(message);
        }
        return null;
    }

    public String handleDAOException(final DAOException daoException) {
        final PostgreSQLErrorCode errorCode = daoException.getPostgreSQLErrorCode();
        final StatusMessages messages = getMessagesHandler();
        if (errorCode != null) {
            final String ret = errorCode.toString();
            messages.clearGlobalMessages();
            messages.add(daoException.getLocalizedMessage());
            return ret;
        } else {
            final Throwable cause = daoException.getCause();
            if (cause instanceof ConstraintViolationException) {
                return handleBeanViolationException((ConstraintViolationException) cause);
            } else {
                messages.add(StatusMessage.Severity.ERROR, format("Erro ao gravar: {0}", cause.getMessage()), cause);
            }
        }
        return null;
    }
    
    private StatusMessages getMessagesHandler() {
    	return FacesMessages.instance();
    }
}
