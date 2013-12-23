package br.com.infox.epp.fluxo.dao;

import static br.com.infox.epp.fluxo.query.DefinicaoVariavelProcessoQuery.*;

import java.util.List;

import javax.persistence.TypedQuery;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;

@Scope(ScopeType.EVENT)
@AutoCreate
@Name(DefinicaoVariavelProcessoDAO.NAME)
public class DefinicaoVariavelProcessoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "definicaoVariavelProcessoDAO";
	
	public List<DefinicaoVariavelProcesso> listVariaveisByFluxo(Fluxo fluxo) {
		return createQueryVariaveisProcessoByFluxo(fluxo).getResultList();
	}
	
	public List<DefinicaoVariavelProcesso> listVariaveisByFluxo(Fluxo fluxo, int start, int count) {
		return createQueryVariaveisProcessoByFluxo(fluxo)
				.setFirstResult(start)
				.setMaxResults(count)
				.getResultList();
	}

	public Long getTotalVariaveisByFluxo(Fluxo fluxo) {
		return getEntityManager().createQuery(TOTAL_BY_FLUXO_QUERY, Long.class)
				.setParameter(QUERY_PARAM_FLUXO, fluxo)
				.getSingleResult();
	}
	
	private TypedQuery<DefinicaoVariavelProcesso> createQueryVariaveisProcessoByFluxo(Fluxo fluxo) {
		return getEntityManager().createQuery(LIST_BY_FLUXO_QUERY, DefinicaoVariavelProcesso.class)
				.setParameter(QUERY_PARAM_FLUXO, fluxo);
	}

	public DefinicaoVariavelProcesso getDefinicao(Fluxo fluxo, String nome) {
		return getEntityManager().createQuery(DEFINICAO_BY_FLUXO_NOME_QUERY, DefinicaoVariavelProcesso.class)
				.setParameter(QUERY_PARAM_FLUXO, fluxo)
				.setParameter(QUERY_PARAM_NOME, nome)
				.getSingleResult();
	}
}
