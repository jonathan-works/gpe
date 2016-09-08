package br.com.infox.epp.system;

import java.util.Properties;

import javax.sql.DataSource;

public interface Database {
    
    String getHibernateDialect();

    String getQuartzDialect();
    
    DatabaseType getDatabaseType();
    
    DataSource getJtaDataSource(String persistenceUnitName);
    
    void performJpaCustomizedProperties(Properties properties);
    
    public enum DatabaseType {
        
        PostgreSQL, SQLServer, Oracle
        
    }
}
