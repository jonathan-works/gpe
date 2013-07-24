package br.com.infox.ibpm.manager;

import javax.persistence.TransactionRequiredException;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.dao.TarefaJbpmDAO;

@Name(TarefaJbpmManager.NAME)
@AutoCreate
public class TarefaJbpmManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tarefaJbpmManager";
	
	@In private TarefaJbpmDAO tarefaJbpmDAO;
	
	public void inserirVersoesTarefas() throws IllegalStateException, TransactionRequiredException {
		tarefaJbpmDAO.inserirVersoesTarefas();
	}

}
