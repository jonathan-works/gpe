package br.com.infox.epa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epa.query.FluxoQuery;
import br.com.infox.ibpm.entity.Fluxo;

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
	
}