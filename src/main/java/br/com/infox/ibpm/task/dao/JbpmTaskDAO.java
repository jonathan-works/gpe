package br.com.infox.ibpm.task.dao;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Query;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.core.dao.DAO;
import br.com.infox.ibpm.util.JbpmUtil;

@Name(JbpmTaskDAO.NAME)
@AutoCreate
public class JbpmTaskDAO extends DAO<Void> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "jbpmTaskDAO";

    public void atualizarTarefasModificadas(Map<Number, String> modifiedTasks) {
        if (!modifiedTasks.isEmpty()) {
            String update = "update jbpm_task set name_ = :taskName where id_ = :taskId";
            Query q = JbpmUtil.getJbpmSession().createSQLQuery(update);
            for (Entry<Number, String> e : modifiedTasks.entrySet()) {
                q.setParameter("taskName", e.getValue());
                q.setParameter("taskId", e.getKey());
                q.executeUpdate();
            }
        }
        JbpmUtil.getJbpmSession().flush();
    }

    public Number findTaskIdByIdProcessDefinitionAndName(Number idProcessDefinition, String taskName) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Number> cq = cb.createQuery(Number.class);
        Root<Task> task = cq.from(Task.class);
        cq.select(cb.max(task.<Long>get("id")));
        cq.where(
                cb.equal(task.<ProcessDefinition>get("processDefinition").<Long>get("id"), cb.literal(idProcessDefinition)),
                cb.equal(task.get("name"), cb.literal(taskName))
        );
        List<Number> result = getEntityManager().createQuery(cq).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

}
