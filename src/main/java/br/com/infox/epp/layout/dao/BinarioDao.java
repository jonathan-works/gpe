package br.com.infox.epp.layout.dao;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.layout.entity.Binario;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class BinarioDao extends Dao<Binario, Integer>{

	public BinarioDao() {
		super(Binario.class);
	}

	@Override
	public EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManagerBin();
	}
	
	public void removeById(Integer id) {
		Binario binario = new Binario();
		binario.setId(id);
		remove(binario);
	}
	
}
