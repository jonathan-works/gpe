package br.com.infox.ibpm.task.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
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
    }

}
