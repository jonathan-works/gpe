package br.com.infox.core.persistence;

import java.sql.SQLException;

import br.com.infox.hibernate.sqlserver.error.SQLServer2012ErrorCode;

public class SqlServer2012ErrorCodeAdaptor {

    public GenericDatabaseErrorCode resolve(SQLException sqlException) {
        int code = sqlException.getErrorCode();
        for (SQLServer2012ErrorCode errorCode : SQLServer2012ErrorCode.values()) {
            if (errorCode.getCode() == code) {
                switch (errorCode) {
                case UNIQUE_VIOLATION:
                    return GenericDatabaseErrorCode.UNIQUE_VIOLATION;
                case NOT_NULL_VIOLATION:
                    return GenericDatabaseErrorCode.NOT_NULL_VIOLATION;
                case FOREIGN_KEY_VIOLATION:
                    return GenericDatabaseErrorCode.FOREIGN_KEY_VIOLATION;
                default:
                    return null;
                }
            }
        }
        return null;
    }
    
}
