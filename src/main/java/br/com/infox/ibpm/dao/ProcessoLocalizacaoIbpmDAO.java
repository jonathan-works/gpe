package br.com.infox.ibpm.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.query.ProcessoLocalizacaoIbpmQuery;

@Name(ProcessoLocalizacaoIbpmDAO.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessoLocalizacaoIbpmDAO extends GenericDAO {

	public static final String NAME = "processoLocalizacaoIbpmDAO";

	public List<Localizacao> listByTaskInstance(Long idTaskInstance) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(ProcessoLocalizacaoIbpmQuery.QUERY_PARAM_ID_TASK_INSTANCE, 
				idTaskInstance);
		List<Localizacao> resultList = getNamedResultList(
				ProcessoLocalizacaoIbpmQuery.LIST_BY_TASK_INSTANCE, parameters);
		return resultList;
	}
	
}