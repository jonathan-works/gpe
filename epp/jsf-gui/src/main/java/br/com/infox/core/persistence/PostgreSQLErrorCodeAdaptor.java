package br.com.infox.core.persistence;

import br.com.infox.hibernate.postgres.error.PostgreSQLErrorCode;

public class PostgreSQLErrorCodeAdaptor implements DatabaseErrorCodeAdapter {
    
    public static final DatabaseErrorCodeAdapter INSTANCE = new PostgreSQLErrorCodeAdaptor();

    public GenericDatabaseErrorCode resolve(Integer errorCode, String sqlState) {
        for (PostgreSQLErrorCode code : PostgreSQLErrorCode.values()) {
            if (code.getCode().equals(sqlState)) {
                switch (code) {
                case UNIQUE_VIOLATION:
                    return GenericDatabaseErrorCode.UNIQUE_VIOLATION;
                case FOREIGN_KEY_VIOLATION:
                    return GenericDatabaseErrorCode.FOREIGN_KEY_VIOLATION;
                case CHECK_VIOLATION:
                    return GenericDatabaseErrorCode.CHECK_VIOLATION;
                case NOT_NULL_VIOLATION:
                    return GenericDatabaseErrorCode.NOT_NULL_VIOLATION;
                default:
                    return null;
                }
            }
        }
        return null;
    }

}
