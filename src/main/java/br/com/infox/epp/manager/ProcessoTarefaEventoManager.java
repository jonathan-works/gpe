package br.com.infox.epp.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.bpm.TaskInstance;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.ProcessoTarefaEventoDAO;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.TarefaEvento;
import br.com.infox.ibpm.jbpm.JbpmUtil;

@Name(ProcessoTarefaEventoManager.NAME)
@AutoCreate
public class ProcessoTarefaEventoManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoTarefaEventoManager";
	
	@In private ProcessoTarefaEventoDAO processoTarefaEventoDAO;
	
	public void destroyProcessoTarefaEvento(){
		Processo processo = JbpmUtil.getProcesso();
		String task = TaskInstance.instance().getTask().getName();
		String fluxo = TaskInstance.instance().getProcessInstance().getProcessDefinition().getName();
		processoTarefaEventoDAO.destroyProcessoTarefaEvento(processo, task, fluxo);
	}
	
	public void marcarProcessoTarefaEventoComoRegistrado(TarefaEvento tarefaEvento){
		Processo processo = JbpmUtil.getProcesso();
		processoTarefaEventoDAO.marcarProcessoTarefaEventoComoRegistrado(tarefaEvento, processo);
	}
	
	public boolean existemEventosNaoRegistrados(){
		Processo processo = JbpmUtil.getProcesso();
		String task = TaskInstance.instance().getTask().getName();
		String fluxo = TaskInstance.instance().getProcessInstance().getProcessDefinition().getName();
		return processoTarefaEventoDAO.existemEventosNaoRegistrados(processo, task, fluxo);
	}
	
}
