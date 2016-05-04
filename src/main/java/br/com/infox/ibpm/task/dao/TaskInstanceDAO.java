package br.com.infox.ibpm.task.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.context.exe.variableinstance.LongInstance;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;

@Stateless
@AutoCreate
@Name(TaskInstanceDAO.NAME)
public class TaskInstanceDAO extends DAO<UsuarioTaskInstance> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "taskInstanceDAO";

    public void removeUsuario(final Long idTaskInstance) throws DAOException {
        remove(find(idTaskInstance));
    }
    
    public TaskInstance getTaskInstanceOpen(Integer idProcesso) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<TaskInstance> cq = cb.createQuery(TaskInstance.class);
        Root<TaskInstance> taskInstance = cq.from(TaskInstance.class);
        Root<LongInstance> variableInstance = cq.from(LongInstance.class);
        Join<TaskInstance, ProcessInstance> processInstance = taskInstance.join("processInstance", JoinType.INNER);
        cq.select(taskInstance);
        cq.where(
                cb.equal(variableInstance.get("processInstance").<Long>get("id"), processInstance.<Long>get("id")),
                cb.equal(variableInstance.<String>get("name"), cb.literal("processo")),
                cb.equal(variableInstance.<Long>get("value"), cb.literal(idProcesso)),
                cb.isNull(processInstance.<Date>get("end")),
                cb.isTrue(taskInstance.<Boolean>get("isOpen")),
                cb.isFalse(taskInstance.<Boolean>get("isSuspended"))
        );
        return getSingleResult(getEntityManager().createQuery(cq));
    }
    
    public List<TaskInstance> getTaskInstancesOpen(Long processDefinitionId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<TaskInstance> cq = cb.createQuery(TaskInstance.class);
        Root<TaskInstance> taskInstance = cq.from(TaskInstance.class);
        Join<TaskInstance, ProcessInstance> processInstance = taskInstance.join("processInstance", JoinType.INNER);
        cq.select(taskInstance);
        cq.where(
                cb.isNull(taskInstance.<Date>get("end")),
                cb.isNotNull(taskInstance.<Date>get("create")),
                cb.isNull(processInstance.<Date>get("end")),
                cb.equal(processInstance.<ProcessDefinition>get("processDefinition").get("id"), cb.literal(processDefinitionId))
        );
        return getEntityManager().createQuery(cq).getResultList();
    }

}
