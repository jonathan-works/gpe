package br.com.infox.core.persistence;

import java.sql.SQLException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.util.PostgreSQLErrorCode;

@Name(PostgreSQLExceptionManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class PostgreSQLExceptionManager {
	public static final String NAME = "postgreSQLExceptionManager";
	
	public PostgreSQLErrorCode discoverErrorCode(Throwable throwable){
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
