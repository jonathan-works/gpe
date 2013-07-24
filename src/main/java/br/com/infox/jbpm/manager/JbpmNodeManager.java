package br.com.infox.jbpm.manager;

import java.math.BigInteger;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.jbpm.dao.JbpmNodeDAO;

@Name(JbpmNodeManager.NAME)
@AutoCreate
public class JbpmNodeManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "jbpmNodeManager";
	
	@In private JbpmNodeDAO jbpmNodeDAO;
	
	public void atualizarNodesModificados(Map<BigInteger, String> modifiedNodes){
		jbpmNodeDAO.atualizarNodesModificados(modifiedNodes);
	}
	
	public BigInteger findNodeIdByIdProcessDefinitionAndName(BigInteger idProcessDefinition, String taskName){
		return jbpmNodeDAO.findNodeIdByIdProcessDefinitionAndName(idProcessDefinition, taskName);
	}

}
