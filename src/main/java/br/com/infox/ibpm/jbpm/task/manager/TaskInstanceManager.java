package br.com.infox.ibpm.jbpm.task.manager;


import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.ibpm.jbpm.dao.TaskInstanceDAO;

@Name(TaskInstanceManager.NAME)
@AutoCreate
public class TaskInstanceManager extends GenericManager {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "taskInstanceManager";

    @In TaskInstanceDAO taskInstanceDAO;
    
    public TaskInstanceManager() {
    }
    
    public void removeUsuario(final Long idTaskInstance) throws DAOException {
        taskInstanceDAO.removeUsuario(idTaskInstance);
    }

}
