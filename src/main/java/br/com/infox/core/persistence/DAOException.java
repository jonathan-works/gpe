package br.com.infox.core.persistence;

import java.sql.SQLException;

import org.jboss.seam.annotations.ApplicationException;

import br.com.infox.epp.system.Configuration;
import br.com.infox.epp.system.Database;

@ApplicationException(end = false, rollback = false)
@javax.ejb.ApplicationException(rollback = true)
public class DAOException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public static final String MSG_UNIQUE_VIOLATION = "constraintViolation.uniqueViolation";
    public static final String MSG_FOREIGN_KEY_VIOLATION = "constraintViolation.foreignKeyViolation";
    public static final String MSG_NOT_NULL_VIOLATION = "constraintViolation.notNullViolation";
    public static final String MSG_CHECK_VIOLATION = "constraintViolation.checkViolation";
    public static final String MSG_DEADLOCK = "databaseError.deadlock";

    private GenericDatabaseErrorCode databaseErrorCode;
    private String localizedMessage;
    private SQLException sqlException;

    public DAOException() {
    }

    public DAOException(String message) {
        super(message);
    }

    public DAOException(Throwable cause) {
        super(cause);
        this.databaseErrorCode = discoverErrorCode(cause);
        setLocalizedMessage();
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
        this.databaseErrorCode = discoverErrorCode(cause);
        setLocalizedMessage();
    }

    public GenericDatabaseErrorCode getDatabaseErrorCode() {
        return databaseErrorCode;
    }

    @Override
    public String getLocalizedMessage() {
        return this.localizedMessage;
    }

    public SQLException getSqlException() {
        return sqlException;
    }

    private void setLocalizedMessage() {
        if (this.databaseErrorCode == null) {
            return;
        }

        switch (this.databaseErrorCode) {
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
            case DEADLOCK:
                this.localizedMessage = MSG_DEADLOCK;
            break;
            default:
                this.localizedMessage = null;
            break;
        }
    }

    private GenericDatabaseErrorCode discoverErrorCode(Throwable throwable) {
        Throwable current = throwable;
        while (current != null && !(current instanceof SQLException)) {
            current = current.getCause();
        }
        if (current != null) {
        	this.sqlException = (SQLException) current;
        	Database database = Configuration.getInstance().getDatabase();
        	return database.getErrorCodeAdapter().resolve(sqlException);
        }
        return null;
    }
}
