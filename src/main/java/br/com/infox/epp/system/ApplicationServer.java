package br.com.infox.epp.system;

import java.util.Properties;

import javax.mail.Session;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

public interface ApplicationServer {
    
    DataSource getListaDadosDataSource();
    
    DataSource getEpaDataSource();

    DataSource getEpaBinDataSource();
    
    DataSource getQuartzDataSource();
    
    String getQuartzDataSourceJndi();
    
    String getEpaDataSourceJndi();
    
    String getUserTransactionJndi();
    
    String getHibernateCacheRegionClass();

    String getHibernateJtaPlataform();
    
    Session getMailSession();
    
    TransactionManager getTransactionManager();
    
    String getInstanceName();
    
    String getLogDir();
    
    void performJpaCustomProperties(Properties properties);
    
    void performQuartzProperties(Properties properties);
    
}
