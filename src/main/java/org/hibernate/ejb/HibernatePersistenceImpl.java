package org.hibernate.ejb;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

import br.com.infox.core.persistence.PersistenceUnitInfoWrapper;
import br.com.infox.epp.system.Configuration;

public class HibernatePersistenceImpl implements PersistenceProvider {
    
    private PersistenceProvider delegate;
    private Configuration configuration;
    
    public HibernatePersistenceImpl() {
        delegate = new HibernatePersistence();
        configuration = Configuration.createInstance();
    }

    @Override
    @SuppressWarnings({ "rawtypes" })
    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties) {
        PersistenceUnitInfoWrapper persistenceUnitInfo = new PersistenceUnitInfoWrapper(info, configuration);
        EntityManagerFactory entityManagerFactory = delegate.createContainerEntityManagerFactory(persistenceUnitInfo, properties);
        return entityManagerFactory;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public EntityManagerFactory createEntityManagerFactory(String emName, Map map) {
        return delegate.createEntityManagerFactory(emName, map);
    }

    @Override
    public ProviderUtil getProviderUtil() {
        return delegate.getProviderUtil();
    }

}
