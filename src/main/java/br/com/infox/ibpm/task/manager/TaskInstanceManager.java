package br.com.infox.ibpm.task.manager;

import javax.ejb.Stateless;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.service.VariaveisJbpmProcessosGerais;
import br.com.infox.ibpm.task.dao.TaskInstanceDAO;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;

@Stateless
@Name(TaskInstanceManager.NAME)
public class TaskInstanceManager extends Manager<TaskInstanceDAO, UsuarioTaskInstance> {
    
    private static final long serialVersionUID = 1L;
    public static final String NAME = "taskInstanceManager";

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
    
    public void atribuirTarefa(Long idTaskInstance) {
        TaskInstance taskInstance = ManagedJbpmContext.instance().getTaskInstance(idTaskInstance);
        ManagedJbpmContext.instance().getSession().buildLockRequest(LockOptions.READ).setLockMode(LockMode.PESSIMISTIC_FORCE_INCREMENT).lock(taskInstance);
        taskInstance.setAssignee(Actor.instance().getId());
    }
    
    public TaskInstance getTaskInstanceOpen(Processo processo) {
        return getDao().getTaskInstanceOpen(processo.getIdProcesso());
    }

}
