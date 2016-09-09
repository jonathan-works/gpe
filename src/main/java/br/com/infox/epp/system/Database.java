package br.com.infox.epp.system;

import java.util.Properties;
import java.util.UUID;

import javax.sql.DataSource;

import org.hibernate.type.SingleColumnType;

import br.com.infox.core.persistence.DatabaseErrorCodeAdapter;

public interface Database {
    
    String getHibernateDialect();

    String getQuartzDelegate();
    
    DatabaseType getDatabaseType();
    
    DataSource getJtaDataSource(String persistenceUnitName);
    
    void performJpaCustomProperties(Properties properties);
    
    void performQuartzProperties(Properties properties);
    
    DatabaseErrorCodeAdapter getErrorCodeAdapter();
    
    SingleColumnType<UUID> getUUIDType(); 
    
    public enum DatabaseType {
        
        PostgreSQL, SQLServer, Oracle
        
    }
}
