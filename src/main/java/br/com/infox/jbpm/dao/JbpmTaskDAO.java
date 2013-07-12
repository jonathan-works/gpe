package br.com.infox.jbpm.dao;

import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.TransactionRequiredException;

import org.hibernate.Query;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.itx.util.EntityUtil;

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
	
	/**
	 * Popula a tabela tb_tarefa com todas as tarefas de todos os fluxos, 
	 * considerando como chave o nome da tarefa task.name_
	 */
	public void encontrarNovasTarefas() throws IllegalStateException, TransactionRequiredException{
		String hql = "insert into public.tb_tarefa (id_fluxo, ds_tarefa) " +
						"select f.id_fluxo, t.name_ from jbpm_task t " +
							"inner join jbpm_processdefinition pd on (pd.id_ = t.processdefinition_) " +
							"inner join public.tb_fluxo f on (f.ds_fluxo = pd.name_) " +
							"inner join jbpm_node jn on (t.tasknode_ = jn.id_ and jn.class_ = 'K') " +
							"where pd.id_ = t.processdefinition_ and not exists " +
								"(select 1 from public.tb_tarefa where ds_tarefa = t.name_ and id_fluxo = f.id_fluxo) " +
							"group by f.id_fluxo, t.name_";
		EntityUtil.getEntityManager().createNativeQuery(hql).executeUpdate();
	}

}
