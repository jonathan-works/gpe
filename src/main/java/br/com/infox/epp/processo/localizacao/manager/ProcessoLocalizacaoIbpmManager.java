package br.com.infox.epp.processo.localizacao.manager;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.manager.PerfilTemplateManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.localizacao.dao.ProcessoLocalizacaoIbpmDAO;
import br.com.infox.epp.processo.localizacao.entity.ProcessoLocalizacaoIbpm;
import br.com.infox.epp.processo.manager.ProcessoManager;

@Name(ProcessoLocalizacaoIbpmManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
@Transactional
public class ProcessoLocalizacaoIbpmManager implements Serializable {
	
    private static final long serialVersionUID = 1L;

    public static final String NAME = "processoLocalizacaoIbpmManager";

    @In
    private ProcessoLocalizacaoIbpmDAO processoLocalizacaoIbpmDAO;
    @In
    private PerfilTemplateManager perfilTemplateManager;
    @In
    private ProcessoManager processoManager;

    public Localizacao listByTaskInstance(Long idTaskInstance) {
        return processoLocalizacaoIbpmDAO.listByTaskInstance(idTaskInstance);
    }

    public void deleteByTaskIdAndProcessId(Long taskId, Long processId) throws DAOException {
        processoLocalizacaoIbpmDAO.deleteProcessoLocalizacaoIbpmByTaskIdAndProcessId(taskId, processId);
    }
    
    public void deleteProcessoLocalizacaoIbpmByTaskInstanceId(Long taskInstanceId) throws DAOException {
    	processoLocalizacaoIbpmDAO.deleteProcessoLocalizacaoIbpmByTaskInstanceId(taskInstanceId);
    }
    
    public void addProcessoLocalizacaoIbpmByTaskInstance(TaskInstance taskInstance) throws DAOException {
    	String[] stringIds = taskInstance.getTask().getSwimlane().getPooledActorsExpression().split(",");
    	for (String stringId : stringIds) {
    		Integer id = Integer.valueOf(stringId);
    		PerfilTemplate perfilTemplate = perfilTemplateManager.find(id);
    		Long idProcessInstance = getRootProcessInstance(taskInstance.getProcessInstance()).getId();
    		Processo processo = processoManager.getProcessoEpaByIdJbpm(idProcessInstance);
    		ProcessoLocalizacaoIbpm processoLocalizacaoIbpm = create(perfilTemplate, processo, taskInstance);
    		processoLocalizacaoIbpmDAO.persist(processoLocalizacaoIbpm);
    	}
    }
    
    public ProcessInstance getRootProcessInstance(ProcessInstance processInstance) {
    	while (processInstance.getSuperProcessToken() != null) {
			processInstance = processInstance.getSuperProcessToken().getProcessInstance();
		}
    	return processInstance;    	
    }
    
    public ProcessoLocalizacaoIbpm create(PerfilTemplate perfilTemplate, Processo processo, TaskInstance taskInstance) {
    	ProcessoLocalizacaoIbpm processoLocalizacaoIbpm = new ProcessoLocalizacaoIbpm();
    	processoLocalizacaoIbpm.setIdTaskInstance(taskInstance.getId());
    	processoLocalizacaoIbpm.setIdTaskJbpm(taskInstance.getTask().getId());
    	processoLocalizacaoIbpm.setLocalizacao(perfilTemplate.getLocalizacao());
    	processoLocalizacaoIbpm.setPapel(perfilTemplate.getPapel());
    	processoLocalizacaoIbpm.setContabilizar(true);
    	processoLocalizacaoIbpm.setProcesso(processo);
    	processoLocalizacaoIbpm.setIdProcessInstanceJbpm(processo.getIdJbpm());
    	return processoLocalizacaoIbpm;
    }

}
