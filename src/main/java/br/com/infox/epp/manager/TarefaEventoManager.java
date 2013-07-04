package br.com.infox.epp.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.bpm.TaskInstance;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.TarefaEventoDAO;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.entity.TarefaEvento;
import br.com.infox.ibpm.jbpm.JbpmUtil;

@Name(TarefaEventoManager.NAME)
@AutoCreate
public class TarefaEventoManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tarefaEventoManager";
	
	@In private TarefaEventoDAO tarefaEventoDAO;
	
	public TarefaEvento getNextTarefaEvento(){
		Processo processo = JbpmUtil.getProcesso();
		String task = TaskInstance.instance().getTask().getName();
		String fluxo = TaskInstance.instance().getProcessInstance().getProcessDefinition().getName();
		return tarefaEventoDAO.getNextTarefaEvento(processo, task, fluxo);
	}

}
