package br.com.infox.epp.system;

import java.util.Properties;

import javax.sql.DataSource;

public interface Database {
    
    String getHibernateDialect();

    String getQuartzDelegate();
    
    DatabaseType getDatabaseType();
    
    DataSource getJtaDataSource(String persistenceUnitName);
    
    void performJpaCustomProperties(Properties properties);
    
    void performQuartzProperties(Properties properties);
    
    public enum DatabaseType {
        
        PostgreSQL, SQLServer, Oracle
        
    }
}
