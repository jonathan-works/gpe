package br.com.infox.epp.localizacao;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.entity.Estrutura_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EstruturaSearch implements Serializable {

	private static final long serialVersionUID = 1L;

	public Estrutura getEstruturaByNome(String codigoEstrutura) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Estrutura> cq = cb.createQuery(Estrutura.class);
		Root<Estrutura> estrutura = cq.from(Estrutura.class);
		cq = cq.select(estrutura).where(cb.equal(estrutura.get(Estrutura_.nome), codigoEstrutura));
		return getEntityManager().createQuery(cq).getSingleResult();
	}

	private EntityManager getEntityManager(){
		return EntityManagerProducer.getEntityManager();
	}

}
