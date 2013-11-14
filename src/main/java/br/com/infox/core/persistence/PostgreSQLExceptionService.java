package br.com.infox.core.persistence;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.util.PostgreSQLErrorCode;

@Name(PostgreSQLExceptionService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class PostgreSQLExceptionService {
	public static final String NAME = "postgreSQLExceptionService";

	@In
	private PostgreSQLExceptionManager postgreSQLExceptionManager;
	
	@In
	private PostgreSQLErrorMessagesService postgreSQLErrorMessagesService;
	
	public String getMessageForError(Throwable t) {
		PostgreSQLErrorCode errorCode = postgreSQLExceptionManager.discoverErrorCode(t);
		if (errorCode != null) {
			return postgreSQLErrorMessagesService.getMessageForError(errorCode);
		}
		return null;
	}
	
	public String getMessageForError(PostgreSQLErrorCode errorCode) {
		return postgreSQLErrorMessagesService.getMessageForError(errorCode);
	}
	
	public PostgreSQLErrorCode getErrorCode(Throwable t) {
		return postgreSQLExceptionManager.discoverErrorCode(t);
	}
}
