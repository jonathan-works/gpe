package br.com.infox.ibpm.node.manager;

import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.ibpm.node.dao.JbpmNodeDAO;

@Stateless
@AutoCreate
@Name(JbpmNodeManager.NAME)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JbpmNodeManager extends Manager<JbpmNodeDAO, Void> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "jbpmNodeManager";

    public void atualizarNodesModificados(Map<Number, String> modifiedNodes) {
        getDao().atualizarNodesModificados(modifiedNodes);
    }

    public Number findNodeIdByIdProcessDefinitionAndName(
            Number idProcessDefinition, String taskName) {
        return getDao().findNodeIdByIdProcessDefinitionAndName(idProcessDefinition, taskName);
    }

}
