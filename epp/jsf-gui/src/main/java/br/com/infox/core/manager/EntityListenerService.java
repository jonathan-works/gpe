package br.com.infox.core.manager;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import br.com.infox.epp.cdi.util.Beans;

public class EntityListenerService<D extends EntityListener<D>> {
    
    @PrePersist
    public void prePersist(D object) {
        getReference(object.getServiceClass()).prePersist(object);
    }
    
    @PreUpdate
    public void preUpdate(D object) {
        getReference(object.getServiceClass()).preUpdate(object);
    }
    
    @PreRemove
    public void preRemove(D object) {
        getReference(object.getServiceClass()).preRemove(object);
    }
    
    @PostPersist
    public void postPersist(D object) {
        getReference(object.getServiceClass()).postPersist(object);
    }
    
    @PostUpdate
    public void postUpdate(D object) {
        getReference(object.getServiceClass()).postUpdate(object);
    }
    
    @PostRemove
    public void postRemove(D object) {
        getReference(object.getServiceClass()).postRemove(object);
    }
    
    @PostLoad
    public void postLoad(D object) {
        getReference(object.getServiceClass()).postLoad(object);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private AbstractEntityListener<D> getReference(Class<? extends AbstractEntityListener> serviceClass) {
        return Beans.getReference(serviceClass);
    }

}
