package br.com.infox.ibpm.task.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.seam.util.ComponentUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class MovimentarTarefaService {
	private static final LogProvider LOG = Logging.getLogProvider(MovimentarTarefaService.class);
	
	@Inject
	private SituacaoProcessoDAO situacaoProcessoDAO;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	// TODO Não funciona com fork/join
	public void finalizarTarefaEmAberto(Processo processo) throws DAOException {
		Long idTaskInstance = situacaoProcessoDAO.getIdTaskInstanceByIdProcesso(processo.getIdProcesso());
		if (idTaskInstance == null) {
			LOG.warn("idTaskInstance para o processo " + processo.getNumeroProcesso() + " nulo");
			return;
		}
		TaskInstance taskInstance = ManagedJbpmContext.instance().getTaskInstanceForUpdate(idTaskInstance);
		taskInstance.end();
		atualizarProcessoTarefa(taskInstance);
	}
	
	private void atualizarProcessoTarefa(TaskInstance taskInstance) throws DAOException {
		ProcessoTarefaManager processoTarefaManager = ComponentUtil.getComponent(ProcessoTarefaManager.NAME);
		ProcessoTarefa processoTarefa = processoTarefaManager.getByTaskInstance(taskInstance.getId());
		processoTarefa.setDataFim(taskInstance.getEnd());
		processoTarefaManager.update(processoTarefa);
	}
}
