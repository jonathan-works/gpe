package br.com.infox.ibpm.dao;

import java.util.HashMap;
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
	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoLocalizacaoIbpmDAO";

	public Localizacao listByTaskInstance(Long idTaskInstance) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(ProcessoLocalizacaoIbpmQuery.QUERY_PARAM_ID_TASK_INSTANCE, 
				idTaskInstance);
		return getNamedSingleResult(ProcessoLocalizacaoIbpmQuery.LIST_BY_TASK_INSTANCE, parameters);
	}
	
}