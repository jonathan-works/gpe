package br.com.infox.core.persistence;

import br.com.infox.hibernate.sqlserver.error.SQLServer2012ErrorCode;

public class SqlServer2012ErrorCodeAdaptor implements DatabaseErrorCodeAdapter {
    
    public static final DatabaseErrorCodeAdapter INSTANCE = new SqlServer2012ErrorCodeAdaptor();

    public GenericDatabaseErrorCode resolve(Integer errorCode, String sqlState) {
        for (SQLServer2012ErrorCode sqlServerCode : SQLServer2012ErrorCode.values()) {
            if (sqlServerCode.getCode() == errorCode) {
                switch (sqlServerCode) {
                case UNIQUE_VIOLATION_INDEX:
                    return GenericDatabaseErrorCode.UNIQUE_VIOLATION;
                case UNIQUE_VIOLATION_CONSTRAINT:
                    return GenericDatabaseErrorCode.UNIQUE_VIOLATION;
                case NOT_NULL_VIOLATION:
                    return GenericDatabaseErrorCode.NOT_NULL_VIOLATION;
                case FOREIGN_KEY_VIOLATION:
                    return GenericDatabaseErrorCode.FOREIGN_KEY_VIOLATION;
                case DEADLOCK:
                    return GenericDatabaseErrorCode.DEADLOCK;
                default:
                    return null;
                }
            }
        }
        return null;
    }
    
}
