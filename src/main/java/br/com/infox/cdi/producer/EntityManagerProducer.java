package br.com.infox.cdi.producer;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.infox.cdi.qualifier.BinaryDatabase;


public class EntityManagerProducer {
	
	@Produces
	@PersistenceContext(unitName = "EPAPersistenceUnit")
	private EntityManager entityManager;
	
	@Produces
	@BinaryDatabase
	@PersistenceContext(unitName = "EPABinPersistenceUnit")
	private EntityManager entityManagerBin;

}
