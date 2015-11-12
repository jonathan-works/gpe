package br.com.infox.ibpm.task.manager;

import java.util.Map;

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

    public void atualizarTarefasModificadas(Map<Number, String> modifiedTasks) {
        getDao().atualizarTarefasModificadas(modifiedTasks);
    }

    public Number findTaskIdByIdProcessDefinitionAndName(Number idProcessDefinition, String taskName) {
        return getDao().findTaskIdByIdProcessDefinitionAndName(idProcessDefinition, taskName);
    }

}
