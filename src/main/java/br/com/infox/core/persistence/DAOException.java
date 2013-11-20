package br.com.infox.core.persistence;

import java.sql.SQLException;

import org.jboss.seam.annotations.ApplicationException;

import br.com.infox.util.PostgreSQLErrorCode;

@ApplicationException(end = false, rollback = false)
public class DAOException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private static final String MSG_UNIQUE_VIOLATION = "#{messages['constraintViolation.uniqueViolation']}";
	private static final String MSG_FOREIGN_KEY_VIOLATION = "#{messages['constraintViolation.foreignKeyViolation']}";
	private static final String MSG_NOT_NULL_VIOLATION = "#{messages['constraintViolation.notNullViolation']}";
	private static final String MSG_CHECK_VIOLATION = "#{messages['constraintViolation.checkViolation']}";
	
	private PostgreSQLErrorCode postgreSQLErrorCode;
	private String localizedMessage;

	public DAOException() {
	}
	
	public DAOException(String message) {
		super(message);
	}
	
	public DAOException(Throwable cause) {
		super(cause);
		this.postgreSQLErrorCode = discoverErrorCode(cause);
		setLocalizedMessage();
	}
	
	public DAOException(String message, Throwable cause) {
		super(message, cause);
		this.postgreSQLErrorCode = discoverErrorCode(cause);
		setLocalizedMessage();
	}
	
	public PostgreSQLErrorCode getPostgreSQLErrorCode() {
		return postgreSQLErrorCode;
	}
	
	@Override
	public String getLocalizedMessage() {
		return this.localizedMessage;
	}
	
	private void setLocalizedMessage() {
		if (this.postgreSQLErrorCode == null) {
			return;
		}
		
		switch (this.postgreSQLErrorCode) {
		case UNIQUE_VIOLATION:
			this.localizedMessage = MSG_UNIQUE_VIOLATION;
			break;
		case FOREIGN_KEY_VIOLATION:
			this.localizedMessage = MSG_FOREIGN_KEY_VIOLATION;
			break;
		case NOT_NULL_VIOLATION:
			this.localizedMessage = MSG_NOT_NULL_VIOLATION;
			break;
		case CHECK_VIOLATION:
			this.localizedMessage = MSG_CHECK_VIOLATION;
			break;
		default:
			this.localizedMessage = null;
			break;
		}
	}
	
	private PostgreSQLErrorCode discoverErrorCode(Throwable throwable) {
		Throwable current = throwable;
		while (current != null && !(current instanceof SQLException)) {
			current = current.getCause();
		}
		if (current != null) {
			SQLException sqlException = (SQLException) current;
			String sqlState = sqlException.getSQLState();
			try {
				for (PostgreSQLErrorCode code : PostgreSQLErrorCode.values()) {
					if (code.getCode().equals(sqlState)) {
						return code;
					}
				}
			} catch (IllegalArgumentException | NullPointerException e) {
				return null;
			}
		}
		return null;
	}
}
