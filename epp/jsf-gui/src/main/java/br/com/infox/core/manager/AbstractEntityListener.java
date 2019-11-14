package br.com.infox.core.manager;

import javax.persistence.EntityManager;

import br.com.infox.cdi.producer.EntityManagerProducer;

public abstract class AbstractEntityListener<T extends EntityListener<T>> {
    
    public void prePersist(T object) {
    }
    
    public void preUpdate(T object) {
    }
    
    public void preRemove(T object) {
    }
    
    public void postPersist(T object) {
    }
    
    public void postUpdate(T object) {
    }
    
    public void postRemove(T object) {
    }
    
    public void postLoad(T object) {
    }
    
    public EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }

}
