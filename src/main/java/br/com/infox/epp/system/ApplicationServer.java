package br.com.infox.epp.system;

import java.util.Properties;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;

public interface ApplicationServer {
    
    DataSource getListaDadosDataSource();
    
    DataSource getEpaDataSource();

    DataSource getEpaBinDataSource();
    
    DataSource getQuartzDataSource();
    
    String getHibernateCacheRegionClass();

    String getHibernateJtaPlataform();
    
    String getMailSession();
    
    String getTransactionManagerLookupClass();
    
    TransactionManager getTransactionManager();
    
    String getInstanceName();
    
    String getLogDir();
    
    void performJpaCustomProperties(Properties properties);
    
    void performQuartzProperties(Properties properties);
    
}
