package org.hibernate.ejb;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import br.com.infox.core.persistence.PersistenceUnitInfoWrapper;
import br.com.infox.epp.system.Configuration;

public class HibernatePersistenceImpl extends HibernatePersistence {
    
    @SuppressWarnings({ "rawtypes" })
    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties) {
        Configuration configuration = Configuration.getInstance();
        PersistenceUnitInfoWrapper persistenceUnitInfo = new PersistenceUnitInfoWrapper(info, configuration);
        return super.createContainerEntityManagerFactory(persistenceUnitInfo, properties);
    }
    
}
