package br.com.infox.epp.documento.publicacao;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;

public class LocalPublicacaoSearch extends PersistenceController {

	public LocalPublicacao findByCodigo(String codigo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<LocalPublicacao> cq = cb.createQuery(LocalPublicacao.class);
		Root<LocalPublicacao> from = cq.from(LocalPublicacao.class);
		
		cq.where(cb.equal(from.get(LocalPublicacao_.codigo), codigo));
		try {
			return getEntityManager().createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
}
