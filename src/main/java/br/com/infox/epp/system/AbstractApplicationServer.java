package br.com.infox.epp.system;

import java.util.Properties;

import br.com.infox.core.persistence.NativeScanner;

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
        properties.put("hibernate.ejb.resource_scanner", NativeScanner.class.getName());
    }
    
    @Override
    public void performQuartzProperties(Properties properties) {
        properties.put("org.quartz.jobStore.dataSource", getEpaDataSource());
        properties.put("org.quartz.jobStore.nonManagedTXDataSource", getQuartzDataSource());
    }
}
