package br.com.infox.epp.tarefa.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.tarefa.dao.TarefaJbpmDAO;

@Name(TarefaJbpmManager.NAME)
@AutoCreate
public class TarefaJbpmManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tarefaJbpmManager";
	
	@In private TarefaJbpmDAO tarefaJbpmDAO;
	
	public void inserirVersoesTarefas() {
		tarefaJbpmDAO.inserirVersoesTarefas();
	}

}
