package br.com.infox.epp.redistribuicao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;

public class TipoRedistribuicaoSearch {

	protected EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

	public TipoRedistribuicao getById(Long id) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<TipoRedistribuicao> cq = cb.createQuery(TipoRedistribuicao.class);
		Root<TipoRedistribuicao> tr = cq.from(TipoRedistribuicao.class);

		cq.where(cb.equal(tr.get(TipoRedistribuicao_.id), id));
		
		return getEntityManager().createQuery(cq).getSingleResult();
	}
	
	public boolean existeTipoRedistribuicao(String codigo, Long idIgnorado) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		cq.select(cb.literal(1));
		Root<TipoRedistribuicao> tr = cq.from(TipoRedistribuicao.class);

		cq.where(cb.equal(tr.get(TipoRedistribuicao_.codigo), codigo));
		if (idIgnorado != null) {
			cq.where(cq.getRestriction(), cb.notEqual(tr.get(TipoRedistribuicao_.id), idIgnorado));
		}
		try {
			Integer result = getEntityManager().createQuery(cq).getSingleResult();
			return result == 1;
		} catch (NoResultException nre) {
			return false;
		}
	}

	public List<TipoRedistribuicao> listAtivos() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<TipoRedistribuicao> cq = cb.createQuery(TipoRedistribuicao.class);
		Root<TipoRedistribuicao> tr = cq.from(TipoRedistribuicao.class);

		cq.where(cb.equal(tr.get(TipoRedistribuicao_.ativo), true));
		
		return getEntityManager().createQuery(cq).getResultList();
	}

}
