package br.com.infox.ibpm.task.dao;

import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.ibpm.util.JbpmUtil;

@Name(JbpmTaskDAO.NAME)
@AutoCreate
public class JbpmTaskDAO extends DAO<Void> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "jbpmTaskDAO";

    public void atualizarTarefasModificadas(
            Map<Number, String> modifiedTasks) {
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

    public Number findTaskIdByIdProcessDefinitionAndName(
            Number idProcessDefinition, String taskName) {
        String hql = "select max(id_) from jbpm_task where processdefinition_ = "
                + ":idProcessDefinition and name_ = :taskName";
        return (Number) JbpmUtil.getJbpmSession().createSQLQuery(hql).setParameter("idProcessDefinition", idProcessDefinition).setParameter("taskName", taskName).uniqueResult();
    }

}
