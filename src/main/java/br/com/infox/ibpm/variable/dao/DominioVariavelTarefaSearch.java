package br.com.infox.ibpm.variable.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa_;

@Stateless
public class DominioVariavelTarefaSearch extends PersistenceController{

	public DominioVariavelTarefa findByCodigo(String codigoDominio) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<DominioVariavelTarefa> cq = cb.createQuery(DominioVariavelTarefa.class);
		Root<DominioVariavelTarefa> from = cq.from(DominioVariavelTarefa.class);
		cq.where(cb.equal(from.get(DominioVariavelTarefa_.codigo), codigoDominio));
		List<DominioVariavelTarefa> result = getEntityManager().createQuery(cq).getResultList();
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
}
