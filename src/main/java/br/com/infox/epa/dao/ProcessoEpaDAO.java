package br.com.infox.epa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epa.entity.ProcessoEpa;
import br.com.infox.epa.query.ProcessoEpaQuery;
import br.com.infox.ibpm.entity.Fluxo;

/**
 * Classe DAO para a entidade ProcessoEpa
 * @author Daniel
 *
 */
@Name(ProcessoEpaDAO.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessoEpaDAO extends GenericDAO {

	private static final long serialVersionUID = 8899227886410190168L;
	public static final String NAME = "processoEpaDAO";

	public List<ProcessoEpa> listAllNotEnded() {
		List<ProcessoEpa> resultList = getNamedResultList
			(ProcessoEpaQuery.LIST_ALL_NOT_ENDED, null);
		return resultList;
	}

	public List<ProcessoEpa> listNotEnded(Fluxo fluxo) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ProcessoEpaQuery.PARAM_FLUXO, fluxo);
		return getNamedResultList
					(ProcessoEpaQuery.LIST_NOT_ENDED_BY_FLUXO, map);
	}
	
}