package br.com.infox.jbpm.manager;

import java.math.BigInteger;
import java.util.Map;

import javax.persistence.TransactionRequiredException;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.jbpm.dao.JbpmTaskDAO;

@Name(JbpmTaskManager.NAME)
@AutoCreate
public class JbpmTaskManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "jbpmTaskManager";
	
	@In private JbpmTaskDAO jbpmTaskDAO;
	
	public void atualizarTarefasModificadas(Map<BigInteger, String> modifiedTasks){
		jbpmTaskDAO.atualizarTarefasModificadas(modifiedTasks);
	}
	
	public BigInteger findTaskIdByIdProcessDefinitionAndName(BigInteger idProcessDefinition, String taskName){
		return jbpmTaskDAO.findTaskIdByIdProcessDefinitionAndName(idProcessDefinition, taskName);
	}
	
	/**
	 * Popula a tabela tb_tarefa com todas as tarefas de todos os fluxos, 
	 * considerando como chave o nome da tarefa task.name_
	 */
	public void encontrarNovasTarefas() throws IllegalStateException, TransactionRequiredException {
		jbpmTaskDAO.encontrarNovasTarefas();
	}

}
