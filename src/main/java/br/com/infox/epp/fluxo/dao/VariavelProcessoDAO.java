package br.com.infox.epp.fluxo.dao;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.VariavelProcesso;
import br.com.infox.epp.fluxo.query.VariavelProcessoQuery;

@Scope(ScopeType.EVENT)
@AutoCreate
@Name(VariavelProcessoDAO.NAME)
public class VariavelProcessoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "variavelProcessoDAO";
	
	public List<VariavelProcesso> listVariaveisProcessoByFluxo(Fluxo fluxo, int start, int count) {
		return entityManager.createQuery(VariavelProcessoQuery.LIST_BY_FLUXO_QUERY, VariavelProcesso.class)
				.setParameter(VariavelProcessoQuery.QUERY_PARAM_FLUXO, fluxo)
				.setFirstResult(start)
				.setMaxResults(count)
				.getResultList();
	}

	public Long getTotalVariaveisProcessoByFluxo(Fluxo fluxo) {
		return entityManager.createQuery(VariavelProcessoQuery.TOTAL_BY_FLUXO_QUERY, Long.class)
				.setParameter(VariavelProcessoQuery.QUERY_PARAM_FLUXO, fluxo)
				.getSingleResult();
	}
}
