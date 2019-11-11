package br.com.infox.epp.core.numeracao;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;

@Stateless
public class ControleNumeracaoDao {
	
	@Inject
	@GenericDao
	private Dao<ControleNumeracao, Long> dao;
	
	public Long getNextNumeracao(String key) {
		ControleNumeracao controle = getControleNumeracao(key);
		if (controle == null) {
			controle = new ControleNumeracao();
			controle.setKey(key);
			controle.setProximoNumero(2L);
			dao.persist(controle);
			return 1L;
		} else {
			Long result = controle.getProximoNumero(); 
			controle.setProximoNumero(result + 1);
			dao.update(controle);
			return result;
		}
	}
	
	private ControleNumeracao getControleNumeracao(String key) {
		CriteriaBuilder cb = dao.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ControleNumeracao> cq = cb.createQuery(ControleNumeracao.class);
		Root<ControleNumeracao> from = cq.from(ControleNumeracao.class);
		cq.where(cb.equal(from.get(ControleNumeracao_.key), key));
		try {
			return dao.getEntityManager().createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public boolean hasControleNumeracaoByKey(String key) {
		return getControleNumeracao(key) != null;
	}
}
