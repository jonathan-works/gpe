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
        
        PostgreSQL("PostgreSQL"), SQLServer("Microsoft SQL Server"), Oracle("Oracle");
        
        private String productName;
        
        private DatabaseType(String productName) {
            this.productName = productName;
        }
        
        public static DatabaseType fromProductName(String productName) {
            DatabaseType[] databaseTypes = DatabaseType.values();
            for (DatabaseType databaseType : databaseTypes) {
                if (databaseType.productName.equalsIgnoreCase(productName)) {
                    return databaseType;
                }
            }
            return null;
        }
        
    }
}
