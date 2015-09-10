package br.com.infox.ibpm.task.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.hibernate.Session;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class MovimentarTarefaService {
	
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void finalizarTarefasEmAberto(Processo processo) throws DAOException {
		try {
			Session jbpmSession = ManagedJbpmContext.instance().getSession();
			ProcessInstance processInstance = (ProcessInstance) jbpmSession.get(ProcessInstance.class, processo.getIdJbpm());
			List<TaskInstance> taskInstances = jbpmSession.getNamedQuery("TaskMgmtSession.findTaskInstancesByProcessInstance")
					.setParameter("processInstance", processInstance).list();
			for (TaskInstance taskInstance : taskInstances) {
				taskInstance.end();
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
}
