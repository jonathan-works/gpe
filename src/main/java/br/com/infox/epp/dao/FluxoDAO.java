package br.com.infox.epp.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.query.FluxoQuery;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.itx.util.EntityUtil;

/**
 * Classe DAO para a entidade Fluxo
 * @author tassio
 *
 */
@Name(FluxoDAO.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class FluxoDAO extends GenericDAO {

	private static final long serialVersionUID = -4180114886888382915L;
	public static final String NAME = "fluxoDAO";
	
	/**
	 * Retorna todos os Fluxos ativos
	 * @return lista de fluxos ativos
	 */
	public List<Fluxo> getFluxoList() {
		return getNamedResultList(FluxoQuery.LIST_ATIVOS, null);
	}
	
	public Long quantidadeProcessosAtrasados(Fluxo fluxo) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(FluxoQuery.FLUXO_PARAM, fluxo);
		return getNamedSingleResult(FluxoQuery.COUNT_PROCESSOS_ATRASADOS, map);
	}
	
	public Fluxo getFluxoByDescricao(String descricao){
		String hql = "select o from Fluxo o where o.fluxo like :descricao";
		Query query = EntityUtil.createQuery(hql).setParameter("descricao", descricao);
		return EntityUtil.getSingleResult(query);
	}
	
}