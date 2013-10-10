package br.com.itx.exception;
import java.text.MessageFormat;

import org.jboss.seam.annotations.ApplicationException;
import org.jboss.seam.annotations.exception.Redirect;
import org.jboss.seam.faces.FacesMessages;

@Redirect(viewId="/error.seam")
@ApplicationException(rollback=true, end=true)
public class AplicationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AplicationException() {
		super();
	}
	
	public AplicationException(String cause) {
		super(cause);
	}
	
	public AplicationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public static String createMessage(String action, String method, String className, String project) {
		FacesMessages.instance().clearGlobalMessages();
		return MessageFormat.format("Erro ao {0}.\nMétodo: {1}.\nClasse: {2}.\nProjeto: {3}", 
				  			 action, method, className, project);
	}
	
}