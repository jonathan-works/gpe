package br.com.infox.cdi.producer;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import br.com.infox.cdi.qualifier.BinaryDatabase;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.util.JNDI;


public class EntityManagerProducer {
	
	@Produces
	@ViewScoped
	private EntityManager entityManager() {
		return JNDI.<EntityManagerFactory>lookup("java:comp/env/EPAPersistenceUnit").createEntityManager();
	}
	
	@Produces
	@ViewScoped
	@BinaryDatabase
	private EntityManager entityManagerBin() {
		 return JNDI.<EntityManagerFactory>lookup("java:comp/env/EPABinPersistenceUnit").createEntityManager();
	}

}
