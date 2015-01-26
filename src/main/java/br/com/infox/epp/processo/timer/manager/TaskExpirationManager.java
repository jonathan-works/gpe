package br.com.infox.epp.processo.timer.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.timer.TaskExpiration;
import br.com.infox.epp.processo.timer.dao.TaskExpirationDAO;

@Name(TaskExpirationManager.NAME)
@AutoCreate
public class TaskExpirationManager extends Manager<TaskExpirationDAO, TaskExpiration> {
    static final String NAME = "taskExpirationManager";
    private static final long serialVersionUID = 1L;
    
    public TaskExpiration getByFluxoAndTaskName(Fluxo fluxo, String taskName) {
        return getDao().getByFluxoAndTaskName(fluxo, taskName);
    }

}
