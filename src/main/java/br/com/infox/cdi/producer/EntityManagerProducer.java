package br.com.infox.cdi.producer;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.hibernate.Session;

import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.jpa.EntityManagerImpl;
import br.com.infox.jpa.EntityManagerSerializable;

public class EntityManagerProducer {
    
    private static final Annotation VIEW_ENTITY_MANAGER = new AnnotationLiteral<ViewEntityManager>() {private static final long serialVersionUID = 1L;};
    private static final Annotation LOG_ENTITY_MANAGER = new AnnotationLiteral<LogEntityManager>() {private static final long serialVersionUID = 1L;};
    private static final Annotation BINARY_DATABASE = new AnnotationLiteral<BinaryDatabase>() {private static final long serialVersionUID = 1L;};
    private static final ThreadLocal<EntityManagerSerializable> ENTITY_MANAGER_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<EntityManagerSerializable> BIN_ENTITY_MANAGER_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<EntityManagerSerializable> LOG_ENTITY_MANAGER_LOCAL = new ThreadLocal<>();

	@PersistenceUnit(unitName = "EPAPersistenceUnit")
	private EntityManagerFactory entityManagerFactory;
	
	@PersistenceUnit(unitName = "EPABinPersistenceUnit")
    private EntityManagerFactory entityManagerBinFactory;

	@Produces
	@Named("entityManager")
	private EntityManager createEntityManager() {
	    EntityManagerSerializable entityManager = null;
	    if (BeanManager.INSTANCE.isSessionContextActive()) {
	        try {
	            entityManager = BeanManager.INSTANCE.getReference(EntityManagerSerializable.class, VIEW_ENTITY_MANAGER);
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
    @Named("hibernateSession")
    private Session createHibernateSession() {
	    EntityManager entityManager = EntityManagerProducer.getEntityManager();
        return entityManager.unwrap(Session.class);
    }
	
	@Produces
	@BinaryDatabase
    private EntityManager createEntityManagerBin() {
        return getOrCreateThreadLocalEntityManagerBin();
    }
	
	@Produces
    @LogEntityManager
    private EntityManager createEntityManagerLog() {
        return getOrCreateThreadLocalEntityManagerLog();
    }
	
	@Produces
	@ViewScoped
	@ViewEntityManager
	private EntityManagerSerializable viewEntityManager() {
		return new EntityManagerImpl(entityManagerFactory);
	}

	public void destroyEntityManager(@Disposes @ViewEntityManager EntityManagerSerializable entityManager) {
		if (entityManager.isOpen()) {
			entityManager.close();
		}
	}

	private EntityManagerSerializable getOrCreateThreadLocalEntityManager() {
	    EntityManagerSerializable entityManager = ENTITY_MANAGER_LOCAL.get();
		if (entityManager == null || !entityManager.isOpen()) {
            entityManager = new EntityManagerImpl(entityManagerFactory);
            EntityManagerReaper.getInstance().register(entityManager, Thread.currentThread());
            ENTITY_MANAGER_LOCAL.set(entityManager);
        }
		return entityManager;
	}
	
	private EntityManagerSerializable getOrCreateThreadLocalEntityManagerBin() {
	    EntityManagerSerializable entityManager = BIN_ENTITY_MANAGER_LOCAL.get();
        if (entityManager == null) {
            entityManager = new EntityManagerImpl(entityManagerBinFactory);
            BIN_ENTITY_MANAGER_LOCAL.set(entityManager);
        }
        return entityManager;
	}
	
	private EntityManagerSerializable getOrCreateThreadLocalEntityManagerLog() {
	    EntityManagerSerializable entityManager = LOG_ENTITY_MANAGER_LOCAL.get();
        if (entityManager == null) {
            entityManager = new EntityManagerImpl(entityManagerFactory);
            LOG_ENTITY_MANAGER_LOCAL.set(entityManager);
        }
        return entityManager;
    }
	
	public static void clear() {
	    EntityManagerSerializable entityManager = ENTITY_MANAGER_LOCAL.get();
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        entityManager = BIN_ENTITY_MANAGER_LOCAL.get();
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        entityManager = LOG_ENTITY_MANAGER_LOCAL.get();
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        BIN_ENTITY_MANAGER_LOCAL.remove();
        ENTITY_MANAGER_LOCAL.remove();
        LOG_ENTITY_MANAGER_LOCAL.remove();
    }
	
	public EntityManager getEntityManagerNotManaged() {
	    return new EntityManagerImpl(entityManagerFactory);
	}
	
	public EntityManager getEntityManagerTransactional() {
	    TransactionManager transactionManager = ApplicationServerService.instance().getTransactionManager();
        try {
            Transaction transaction = transactionManager.getTransaction();
            if (transaction == null) {
                throw new IllegalStateException("No transaction ");
            }
            EntityManagerImpl entityManager = new EntityManagerImpl(entityManagerFactory);
            transaction.registerSynchronization(entityManager);
            return entityManager;
        } catch (SystemException | IllegalStateException | RollbackException e) {
            throw new IllegalStateException("Error creating entityManagerTransactional", e);
        }
    }
	
	public EntityManagerFactory getEntityManagerFactory() {
	    return entityManagerFactory;
	}
	
	public static EntityManager getEntityManager() {
	    return BeanManager.INSTANCE.getReference(EntityManager.class);
	}
	
	public static EntityManager getEntityManagerBin() {
        return BeanManager.INSTANCE.getReference(EntityManager.class, BINARY_DATABASE);
    }
	
	public static EntityManager getEntityManagerLog() {
        return BeanManager.INSTANCE.getReference(EntityManager.class, LOG_ENTITY_MANAGER);
    }
	
	public static EntityManagerProducer instance() {
	    return BeanManager.INSTANCE.getReference(EntityManagerProducer.class);
	}
	
	@Qualifier
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
	public @interface LogEntityManager {}
	
	@Qualifier
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
	public @interface ViewEntityManager {}
	
	@Qualifier
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
	public @interface BinaryDatabase {}

}
