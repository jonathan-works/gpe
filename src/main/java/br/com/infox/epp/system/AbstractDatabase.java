package br.com.infox.epp.system;

import java.util.Properties;

import javax.sql.DataSource;

public abstract class AbstractDatabase implements Database {
    
    @Override
    public DataSource getJtaDataSource(String persistenceUnitName) {
        ApplicationServer applicationServer = Configuration.getInstance().getApplicationServer();
        if (Configuration.getInstance().getEpaPersistenceUnitName().equals(persistenceUnitName)) {
            return applicationServer.getEpaDataSource();
        } else if (Configuration.getInstance().getEpaBinPersistenceUnitName().equals(persistenceUnitName)) {
            return applicationServer.getEpaBinDataSource();
        } else {
            throw new IllegalStateException("Unknow persistence unit name " + persistenceUnitName);
        }
    }
    
    @Override
    public void performJpaCustomProperties(Properties properties) {
        properties.put("hibernate.dialect", getHibernateDialect());
    }
    
    @Override
    public void performQuartzProperties(Properties properties) {
        properties.put("org.quartz.jobStore.driverDelegateClass", getQuartzDelegate());
    }
    
}
