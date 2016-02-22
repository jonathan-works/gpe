package br.com.infox.epp.tipoParte;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.entity.TipoParte_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TipoParteSearch {

	public TipoParte getTipoParteByIdentificador(String identificador) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<TipoParte> cq = cb.createQuery(TipoParte.class);
		Root<TipoParte> tipoParte = cq.from(TipoParte.class);

		Predicate identificadorIgual = cb.equal(tipoParte.get(TipoParte_.identificador), identificador);

		cq.select(tipoParte).where(identificadorIgual);

		return getEntityManager().createQuery(cq).getSingleResult();
	}

	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

}
