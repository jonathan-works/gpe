package br.com.infox.epp.localizacao;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Localizacao_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class LocalizacaoSearch {

	public Localizacao getLocalizacaoByCodigo(String codigoLocalizacao) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Localizacao> cq = cb.createQuery(Localizacao.class);
		Root<Localizacao> estrutura = cq.from(Localizacao.class);
		Predicate codigoIgual = cb.equal(estrutura.get(Localizacao_.codigo), codigoLocalizacao);
		Predicate ativo = cb.isTrue(estrutura.get(Localizacao_.ativo));
		cq = cq.select(estrutura).where(cb.and(codigoIgual, ativo));
		return getEntityManager().createQuery(cq).getSingleResult();
	}

	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

}
