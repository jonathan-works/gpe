package br.com.infox.jbpm.dao;

import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.jbpm.JbpmUtil;

@Name(JbpmTaskDAO.NAME)
@AutoCreate
public class JbpmTaskDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "jbpmTaskDAO";
	
	public void atualizarTarefasModificadas(Map<BigInteger, String> modifiedTasks){
		if (modifiedTasks.size() > 0) {
			String update = "update jbpm_task set name_ = :taskName where id_ = :taskId";
			Query q = JbpmUtil.getJbpmSession().createSQLQuery(update);
			for (Entry<BigInteger, String> e : modifiedTasks.entrySet()) {
				q.setParameter("taskName", e.getValue());
				q.setParameter("taskId", e.getKey());
				q.executeUpdate();
			}
		}
		JbpmUtil.getJbpmSession().flush();
	}
	
	public BigInteger findTaskIdByIdProcessDefinitionAndName(BigInteger idProcessDefinition, String taskName){
		String hql = "select max(id_) from jbpm_task where processdefinition_ = "
				+ ":idProcessDefinition and name_ = :taskName";
		return (BigInteger) JbpmUtil.getJbpmSession().createSQLQuery(hql)
							.setParameter("idProcessDefinition", idProcessDefinition)
							.setParameter("taskName", taskName).uniqueResult();
	}

}
