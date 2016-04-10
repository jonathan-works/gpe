package br.com.infox.core.persistence;

import javax.persistence.EntityManager;

import br.com.infox.cdi.producer.EntityManagerProducer;

public abstract class PersistenceController {
    
    public EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
    
    public EntityManager getEntityManagerBin() {
        return EntityManagerProducer.getEntityManagerBin();
    }

}
