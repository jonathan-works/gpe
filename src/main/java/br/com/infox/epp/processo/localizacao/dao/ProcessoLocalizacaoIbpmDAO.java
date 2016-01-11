package br.com.infox.epp.processo.localizacao.dao;

import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.DELETE_BY_PROCESS_ID_AND_TASK_ID;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.DELETE_BY_TASK_INSTANCE_ID;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.LIST_BY_TASK_INSTANCE;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.PARAM_ID_TASK_INSTANCE;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.PARAM_PROCESS_ID;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.PARAM_TASK_ID;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.PARAM_TASK_INSTANCE;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.processo.localizacao.entity.ProcessoLocalizacaoIbpm;

@AutoCreate
@Name(ProcessoLocalizacaoIbpmDAO.NAME)
public class ProcessoLocalizacaoIbpmDAO extends DAO<ProcessoLocalizacaoIbpm> {
	
    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoLocalizacaoIbpmDAO";
    
    public Localizacao listByTaskInstance(Long idTaskInstance) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_ID_TASK_INSTANCE, idTaskInstance);
        return getNamedSingleResult(LIST_BY_TASK_INSTANCE, parameters);
    }

    public void deleteProcessoLocalizacaoIbpmByTaskIdAndProcessId(Long taskId, Long processId) throws DAOException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_PROCESS_ID, processId);
        parameters.put(PARAM_TASK_ID, taskId);
        executeNamedQueryUpdate(DELETE_BY_PROCESS_ID_AND_TASK_ID, parameters);
    }
    
    public void deleteProcessoLocalizacaoIbpmByTaskInstanceId(Long taskInstanceId) throws DAOException {
        Map<String, Object> parameters = new HashMap<>(1);
        parameters.put(PARAM_TASK_INSTANCE, taskInstanceId);
        executeNamedQueryUpdate(DELETE_BY_TASK_INSTANCE_ID, parameters);
    }
    
}
