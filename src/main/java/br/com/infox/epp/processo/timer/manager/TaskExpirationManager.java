package br.com.infox.epp.processo.timer.manager;

import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.timer.TaskExpiration;
import br.com.infox.epp.processo.timer.dao.TaskExpirationDAO;

@Stateless
@AutoCreate
@Name(TaskExpirationManager.NAME)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TaskExpirationManager extends Manager<TaskExpirationDAO, TaskExpiration> {
	
    static final String NAME = "taskExpirationManager";
    private static final long serialVersionUID = 1L;
    
    public TaskExpiration getByFluxoAndTaskName(Fluxo fluxo, String taskName) {
        return getDao().getByFluxoAndTaskName(fluxo, taskName);
    }

    public void updateTransitionName(Fluxo fluxo, String taskName, String oldName, String newName) {
        TaskExpiration te = getDao().getByFluxoAndTaskName(fluxo, taskName);
        if (te != null && te.getTransition().equals(oldName)) {
            te.setTransition(newName);
        }
        flush();
    }

    public void clearUnusedTaskExpirations(Fluxo fluxo, Set<String> taskNames) throws DAOException {
        getDao().clearUnusedTaskExpirations(fluxo, taskNames);
    }
}
