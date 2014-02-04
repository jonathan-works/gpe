package br.com.infox.ibpm.node.manager;

import java.math.BigInteger;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.ibpm.node.dao.JbpmNodeDAO;

@Name(JbpmNodeManager.NAME)
@AutoCreate
public class JbpmNodeManager extends Manager<JbpmNodeDAO, Void> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "jbpmNodeManager";
	
	public void atualizarNodesModificados(Map<BigInteger, String> modifiedNodes){
		getDao().atualizarNodesModificados(modifiedNodes);
	}
	
	public BigInteger findNodeIdByIdProcessDefinitionAndName(BigInteger idProcessDefinition, String taskName){
		return getDao().findNodeIdByIdProcessDefinitionAndName(idProcessDefinition, taskName);
	}

}
