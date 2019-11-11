package br.com.infox.core.persistence;

public interface DatabaseErrorCodeAdapter {
    
    public GenericDatabaseErrorCode resolve(Integer errorCode, String sqlState);

}
