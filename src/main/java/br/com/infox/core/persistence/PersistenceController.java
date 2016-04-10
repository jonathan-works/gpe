package br.com.infox.core.persistence;

import javax.persistence.EntityManager;

import br.com.infox.cdi.producer.EntityManagerProducer;

public abstract class PersistenceController {
    
    protected EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
    
    protected EntityManager getEntityManagerBin() {
        return EntityManagerProducer.getEntityManagerBin();
    }

}
