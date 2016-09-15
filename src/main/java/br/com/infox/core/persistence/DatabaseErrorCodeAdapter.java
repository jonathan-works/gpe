package br.com.infox.core.persistence;

import java.sql.SQLException;

public interface DatabaseErrorCodeAdapter {
    
    public GenericDatabaseErrorCode resolve(SQLException sqlException);

}
