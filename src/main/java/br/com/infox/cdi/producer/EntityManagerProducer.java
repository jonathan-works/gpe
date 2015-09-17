package br.com.infox.cdi.producer;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import br.com.infox.jpa.EntityManagerImpl;
import br.com.infox.cdi.qualifier.BinaryDatabase;
import br.com.infox.cdi.qualifier.ViewEntityManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.config.BeanManager;

public class EntityManagerProducer {

	@PersistenceUnit(unitName = "EPAPersistenceUnit")
	private EntityManagerFactory entityManagerFactory;

	@Produces
	@Named("entityManagerCDI")
	private EntityManager createEntityManager() {
		if (BeanManager.INSTANCE.isSessionContextActive()) {
			Annotation[] annotations = new Annotation[1];
			annotations[0] = new AnnotationLiteral<ViewEntityManager>() {private static final long serialVersionUID = 1L;};
			return BeanManager.INSTANCE.getReference(EntityManager.class, annotations);
		} else {
			return new EntityManagerImpl(entityManagerFactory);
		}
	}

	@Produces
	@ViewScoped
	@ViewEntityManager
	private EntityManager viewEntityManager() {
		return new EntityManagerImpl(entityManagerFactory);
	}

	@Produces
	@BinaryDatabase
	@PersistenceContext(unitName = "EPABinPersistenceUnit")
	private EntityManager entityManagerBin;

	public void destroyEntityManager(@Disposes @ViewEntityManager EntityManager entityManager) {
		if (entityManager.isOpen()) {
			entityManager.close();
		}
	}

}
