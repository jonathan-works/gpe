package br.com.infox.ibpm.task.manager;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.ibpm.task.dao.JbpmTaskDAO;

@Stateless
@AutoCreate
@Name(JbpmTaskManager.NAME)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JbpmTaskManager extends Manager<JbpmTaskDAO, Void> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "jbpmTaskManager";

    public Number findTaskIdByIdProcessDefinitionAndName(Number idProcessDefinition, String taskName) {
        return getDao().findTaskIdByIdProcessDefinitionAndName(idProcessDefinition, taskName);
    }

}
