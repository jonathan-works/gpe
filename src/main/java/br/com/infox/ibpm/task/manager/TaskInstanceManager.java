package br.com.infox.ibpm.task.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.service.VariaveisJbpmProcessosGerais;
import br.com.infox.ibpm.task.dao.TaskInstanceDAO;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;

@Name(TaskInstanceManager.NAME)
@AutoCreate
public class TaskInstanceManager extends Manager<TaskInstanceDAO, UsuarioTaskInstance> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "taskInstanceManager";

    public TaskInstanceManager() {
    }

    public void removeUsuario(final Long idTaskInstance) throws DAOException {
        getDao().removeUsuario(idTaskInstance);
        try {
	        TaskInstance taskInstance = ManagedJbpmContext.instance().getTaskInstanceForUpdate(idTaskInstance);
			taskInstance.deleteVariableLocally(VariaveisJbpmProcessosGerais.OWNER);
			taskInstance.setAssignee(null);
        } catch (Exception e) {
        	throw new DAOException(e);
        }
    }

}
