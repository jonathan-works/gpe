package br.com.infox.ibpm.jbpm.task.manager;

import java.math.BigInteger;
import java.util.Map;

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
	
}
