package br.com.infox.ibpm.task.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.core.dao.DAO;

@Stateless
@AutoCreate
@Name(JbpmTaskDAO.NAME)
public class JbpmTaskDAO extends DAO<Void> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "jbpmTaskDAO";

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
