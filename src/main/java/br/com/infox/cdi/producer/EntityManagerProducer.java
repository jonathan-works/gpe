package br.com.infox.cdi.producer;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import br.com.infox.cdi.qualifier.BinaryDatabase;
import br.com.infox.cdi.qualifier.ViewEntityManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.jpa.EntityManagerImpl;

public class EntityManagerProducer {
    
    private static final Annotation VIEW_ENTITY_MANAGER = new AnnotationLiteral<ViewEntityManager>() {private static final long serialVersionUID = 1L;};
    private static final Annotation BINARY_DATABASE = new AnnotationLiteral<BinaryDatabase>() {private static final long serialVersionUID = 1L;};
    private static final ThreadLocal<EntityManager> ENTITY_MANAGER_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<EntityManager> ENTITY_MANAGER_BIN_LOCAL = new ThreadLocal<>();

	@PersistenceUnit(unitName = "EPAPersistenceUnit")
	private EntityManagerFactory entityManagerFactory;
	
	@PersistenceUnit(unitName = "EPABinPersistenceUnit")
    private EntityManagerFactory entityManagerBinFactory;

	@Produces
	@Named("entityManagerCDI")
	private EntityManager createEntityManager() {
	    EntityManager entityManager = null;
	    if (BeanManager.INSTANCE.isSessionContextActive()) {
	        try {
	            entityManager = BeanManager.INSTANCE.getReference(EntityManager.class, VIEW_ENTITY_MANAGER);
	            entityManager.isOpen();
	        } catch (Exception e) {
	        	entityManager = null;
	        }
	    }
	    if (entityManager == null) {
	    	entityManager = getOrCreateThreadLocalEntityManager();
	    }
	    return entityManager;
	}
	
	@Produces
	@BinaryDatabase
    @Named("entityManagerBinCDI")
    private EntityManager createEntityManagerBin() {
        EntityManager entityManager = null;
        if (BeanManager.INSTANCE.isSessionContextActive()) {
            try {
                entityManager = BeanManager.INSTANCE.getReference(EntityManager.class, new Annotation[]{VIEW_ENTITY_MANAGER, BINARY_DATABASE});
                entityManager.isOpen();
            } catch (Exception e) {
                entityManager = null;
            }
        }
        if (entityManager == null) {
            entityManager = getOrCreateThreadLocalEntityManagerBin();
        }
        return entityManager;
    }
	
	@Produces
	@ViewScoped
	@ViewEntityManager
	private EntityManager viewEntityManager() {
		return new EntityManagerImpl(entityManagerFactory);
	}

	@Produces
	@BinaryDatabase
	@ViewEntityManager
	private EntityManager viewEntityManagerBin() {
	    return new EntityManagerImpl(entityManagerBinFactory);
	}

	public void destroyEntityManager(@Disposes @ViewEntityManager EntityManager entityManager) {
		if (entityManager.isOpen()) {
			entityManager.close();
		}
	}
	
	public void destroyEntityManagerBin(@Disposes @ViewEntityManager @BinaryDatabase EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

	private EntityManager getOrCreateThreadLocalEntityManager() {
		EntityManager entityManager = ENTITY_MANAGER_LOCAL.get();
		if (entityManager == null) {
            entityManager = new EntityManagerImpl(entityManagerFactory);
            ENTITY_MANAGER_LOCAL.set(entityManager);
        }
		return entityManager;
	}
	
	private EntityManager getOrCreateThreadLocalEntityManagerBin() {
	    EntityManager entityManager = ENTITY_MANAGER_BIN_LOCAL.get();
        if (entityManager == null) {
            entityManager = new EntityManagerImpl(entityManagerBinFactory);
            ENTITY_MANAGER_BIN_LOCAL.set(entityManager);
        }
        return entityManager;
	}
	
	public static void clear() {
        EntityManager entityManager = ENTITY_MANAGER_LOCAL.get();
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        entityManager = ENTITY_MANAGER_BIN_LOCAL.get();
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        ENTITY_MANAGER_BIN_LOCAL.set(null);
        ENTITY_MANAGER_LOCAL.set(null);
    }
	
	public static EntityManager getEntityManager() {
	    return BeanManager.INSTANCE.getReference(EntityManager.class);
	}
	
	public static EntityManager getEntityManagerBin() {
        return BeanManager.INSTANCE.getReference(EntityManager.class, BINARY_DATABASE);
    }
}
