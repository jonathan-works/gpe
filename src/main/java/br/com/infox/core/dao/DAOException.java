package br.com.infox.core.dao;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.ApplicationException;

import br.com.infox.core.persistence.PostgreSQLExceptionManager;
import br.com.infox.util.PostgreSQLErrorCode;

@ApplicationException(end = false, rollback = false)
public class DAOException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private PostgreSQLErrorCode postgreSQLErrorCode;

	public DAOException() {
	}
	
	public DAOException(String message) {
		super(message);
	}
	
	public DAOException(Throwable cause) {
		super(cause);
		discoverErrorCode(cause);
	}
	
	public DAOException(String message, Throwable cause) {
		super(message, cause);
		discoverErrorCode(cause);
	}
	
	public PostgreSQLErrorCode getPostgreSQLErrorCode() {
		return postgreSQLErrorCode;
	}
	
	private void discoverErrorCode(Throwable t) {
		PostgreSQLExceptionManager postgreSQLExceptionManager = (PostgreSQLExceptionManager) Component.getInstance(PostgreSQLExceptionManager.NAME);
		this.postgreSQLErrorCode = postgreSQLExceptionManager.discoverErrorCode(t);
	}
}
