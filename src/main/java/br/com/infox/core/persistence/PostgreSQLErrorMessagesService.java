package br.com.infox.core.persistence;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.util.PostgreSQLErrorCode;

@Name(PostgreSQLErrorMessagesService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class PostgreSQLErrorMessagesService {
	public static final String NAME = "postgreSQLErrorMessagesService";
	
	private static final String MSG_UNIQUE_VIOLATION = "#{messages['constraintViolation.uniqueViolation']}";
	private static final String MSG_FOREIGN_KEY_VIOLATION = "#{messages['constraintViolation.foreignKeyViolation']}";
	private static final String MSG_NOT_NULL_VIOLATION = "#{messages['constraintViolation.notNullViolation']}";
	private static final String MSG_CHECK_VIOLATION = "#{messages['constraintViolation.checkViolation']}";
	
	public String getMessageForError(PostgreSQLErrorCode error) {
		switch (error) {
		case UNIQUE_VIOLATION:
			return MSG_UNIQUE_VIOLATION;
		case FOREIGN_KEY_VIOLATION:
			return MSG_FOREIGN_KEY_VIOLATION;
		case NOT_NULL_VIOLATION:
			return MSG_NOT_NULL_VIOLATION;
		case CHECK_VIOLATION:
			return MSG_CHECK_VIOLATION;
		default:
			return null;
		}
	}
}
