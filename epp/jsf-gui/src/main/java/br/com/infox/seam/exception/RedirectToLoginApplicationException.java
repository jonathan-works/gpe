package br.com.infox.seam.exception;

import static java.text.MessageFormat.format;

import org.jboss.seam.annotations.exception.Redirect;
import org.jboss.seam.faces.FacesMessages;

@Redirect(viewId = "/login.xhtml")
@org.jboss.seam.annotations.ApplicationException(rollback = true, end = true)
public class RedirectToLoginApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RedirectToLoginApplicationException() {
        super();
    }

    public RedirectToLoginApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedirectToLoginApplicationException(String message) {
        super(message);
    }

    public RedirectToLoginApplicationException(Throwable cause) {
        super(cause);
    }

    public static String createMessage(String action, String method,
            String className, String project) {
        FacesMessages.instance().clearGlobalMessages();
        return format("Erro ao {0}.\nMétodo: {1}.\nClasse: {2}.\nProjeto: {3}", action, method, className, project);
    }

}
