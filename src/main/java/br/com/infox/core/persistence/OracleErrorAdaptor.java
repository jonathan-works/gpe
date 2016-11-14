package br.com.infox.core.persistence;

import br.com.infox.hibernate.oracle.error.OracleErrorCode;

public class OracleErrorAdaptor implements DatabaseErrorCodeAdapter {
    
    public static final DatabaseErrorCodeAdapter INSTANCE = new OracleErrorAdaptor();

    public GenericDatabaseErrorCode resolve(Integer errorCode, String sqlState) {
        for (OracleErrorCode oracleCode : OracleErrorCode.values()) {
            if (oracleCode.getCode() == errorCode) {
                switch (oracleCode) {
                case UNIQUE_VIOLATION:
                    return GenericDatabaseErrorCode.UNIQUE_VIOLATION;
                case CHECK_CONSTRAINT_VIOLATION:
                    return GenericDatabaseErrorCode.CHECK_VIOLATION;
                case FOREIGN_KEY_VIOLATION_HAS_CHILDREN:
                    return GenericDatabaseErrorCode.FOREIGN_KEY_VIOLATION;
                case NOT_NULL_VIOLATION:
                    return GenericDatabaseErrorCode.NOT_NULL_VIOLATION;
                default:
                    break;
                }
            }
        }
        return null;
    }
    
}
