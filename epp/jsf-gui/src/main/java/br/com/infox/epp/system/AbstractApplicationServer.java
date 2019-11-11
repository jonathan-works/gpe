package br.com.infox.epp.system;

import java.util.Properties;

public abstract class AbstractApplicationServer implements ApplicationServer {
    
    @Override
    public String getLogDir() {
        return System.getProperty("java.io.tmpdir");
    }
    
    @Override
    public String getInstanceName() {
        return System.getProperty("user.name");
    }
    
    @Override
    public void performJpaCustomProperties(Properties properties) {
        properties.put("hibernate.transaction.jta.platform", getHibernateJtaPlataform());
        properties.put("hibernate.cache.region.factory_class", getHibernateCacheRegionClass());
    }
    
    @Override
    public void performQuartzProperties(Properties properties) {
        properties.put("org.quartz.jobStore.dataSource", "epaDataSource");
        properties.put("org.quartz.jobStore.nonManagedTXDataSource", "epaQuartzDataSource");
        properties.put("org.quartz.dataSource.epaDataSource.jndiURL", getEpaDataSourceJndi());
        properties.put("org.quartz.dataSource.epaQuartzDataSource.jndiURL", getQuartzDataSourceJndi());
    }
}
