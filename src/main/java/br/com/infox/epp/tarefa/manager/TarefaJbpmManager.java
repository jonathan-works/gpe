package br.com.infox.epp.tarefa.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.tarefa.dao.TarefaJbpmDAO;
import br.com.infox.epp.tarefa.entity.TarefaJbpm;

@Name(TarefaJbpmManager.NAME)
@AutoCreate
public class TarefaJbpmManager extends Manager<TarefaJbpmDAO, TarefaJbpm> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tarefaJbpmManager";
	
	public void inserirVersoesTarefas() {
		getDao().inserirVersoesTarefas();
	}

}
