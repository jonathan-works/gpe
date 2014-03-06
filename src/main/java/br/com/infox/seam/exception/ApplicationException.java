package br.com.infox.seam.exception;

import java.text.MessageFormat;

import org.jboss.seam.annotations.exception.Redirect;
import org.jboss.seam.faces.FacesMessages;

@Redirect(viewId = "/error.seam")
@org.jboss.seam.annotations.ApplicationException(rollback = true, end = true)
public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ApplicationException() {
        super();
    }

    public ApplicationException(String cause) {
        super(cause);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static String createMessage(String action, String method,
            String className, String project) {
        FacesMessages.instance().clearGlobalMessages();
        return MessageFormat.format("Erro ao {0}.\nMétodo: {1}.\nClasse: {2}.\nProjeto: {3}", action, method, className, project);
    }

}
