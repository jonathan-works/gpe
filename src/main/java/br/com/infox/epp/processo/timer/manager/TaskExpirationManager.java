package br.com.infox.epp.processo.timer.manager;

import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.timer.TaskExpiration;
import br.com.infox.epp.processo.timer.dao.TaskExpirationDAO;
import br.com.infox.ibpm.transition.TransitionHandler;

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

    /**
     * Observer que capta o evento de mudança de nome da transição e atualiza 
     * a data de expiração desta transição, caso exista.
     * 
     * @param idFluxo
     * @param oldName
     * @param newName
     */
    // #72877
    @Observer(TransitionHandler.EVENT_JBPM_TRANSITION_NAME_CHANGED)
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
