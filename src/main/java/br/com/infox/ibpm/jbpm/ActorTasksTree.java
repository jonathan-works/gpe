/*
  IBPM - Ferramenta de produtividade Java
  Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.
 
  Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
  sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
  Free Software Foundation; versão 2 da Licença.
  Este programa é distribuído na expectativa de que seja útil, porém, SEM 
  NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
  ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
  
  Consulte a GNU GPL para mais detalhes.
  Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
  veja em http://www.gnu.org/licenses/  
 */
package br.com.infox.ibpm.jbpm;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.taskmgmt.def.Task;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;


@Name("actorTasksTree")
@SuppressWarnings("unchecked")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class ActorTasksTree implements Serializable {

	private static final String[] TASK_TYPES = {"Minhas tarefas", "Tarefas dos meus grupos"};

	private static final long serialVersionUID = 1L;

	private Map<String, List<Task>> processMap = new HashMap<String, List<Task>>();
	private Map<String, List<Task>> taskInstanceMap = new HashMap<String, List<Task>>();

	private ProcessDefinition currentProcess;

	private Task currentTask;

	public List getProcess(String type) {
		if (processMap.get(type) == null) {
			if (type.equals(TASK_TYPES[0])) {
				processMap.put(type, getMyTaskList());
			} else {
				processMap.put(type, getGroupTaskList());
			}
		}
		return processMap.get(type);
	}

	private List getMyTaskList() {
		Session session = ManagedJbpmContext.instance().getSession();
		StringBuilder sb = new StringBuilder();
		sb.append("select ti.processInstance.processDefinition.name ");
		sb.append(", count(ti.processInstance.processDefinition.name) ");
		sb.append("from ");
		sb.append("org.jbpm.taskmgmt.exe.TaskInstance as ti ");
		sb.append("where ti.actorId = :actorId ");
		sb.append("and ti.isSuspended = false ");
		sb.append("and ti.isOpen = true ");
		sb.append("group by ti.processInstance.processDefinition.name");
		return session.createQuery(sb.toString())
				.setParameter("actorId", Actor.instance().getId()).list();
	}
	
	private List getGroupTaskList() {
		Session session = ManagedJbpmContext.instance().getSession();
		StringBuilder sb = new StringBuilder();
		sb.append("select ti.processInstance.processDefinition.name ");
		sb.append(", count(ti.processInstance.processDefinition.name) ");
		sb.append("from ");
		sb.append("org.jbpm.taskmgmt.exe.TaskInstance as ti ");
		sb.append("where ti.actorId = :actorId ");
		sb.append("and ti.isSuspended = false ");
		sb.append("and ti.isOpen = true ");
		sb.append("group by ti.processInstance.processDefinition.name");
		return session.createQuery(sb.toString())
				.setParameter("actorId", Actor.instance().getId()).list();
	}

	public List<Task> getTasks(String process) {
		if (taskInstanceMap.containsKey(process)) {
			return taskInstanceMap.get(process);
		}
		Session session = ManagedJbpmContext.instance().getSession();
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct ti.task from ");
		sb.append("org.jbpm.taskmgmt.exe.TaskInstance as ti ");
		sb.append("where ti.actorId = :actorId ");
		sb.append("and ti.isSuspended = false ");
		sb.append("and ti.isOpen = true ");
		sb.append("and ti.processInstance.processDefinition.name = :processDefinition");
		List<Task> list = session.createQuery(sb.toString())
				.setParameter("actorId", Actor.instance().getId())
				.setParameter("processDefinition", process)
				.list();
		taskInstanceMap.put(process, list);
		return list;
	}

	public List getTasks() {
		Session session = ManagedJbpmContext.instance().getSession();
		StringBuilder sb = new StringBuilder();
		sb.append("select ti from ")
		  .append("org.jbpm.taskmgmt.exe.TaskInstance as ti ")
		  .append("where ti.actorId = :actorId ")
		  .append("and ti.isSuspended = false ")
		  .append("and ti.isOpen = true");
		if (currentProcess != null) {
			sb.append(" and ti.processInstance.processDefinition = :process");
		}
		if (currentTask != null) {
			sb.append(" and ti.task = :task");
		}
		sb.append(" order by ti.create");
		Query query = session.createQuery(sb.toString());
		query.setParameter("actorId", Actor.instance().getId());
		if (currentProcess != null) {
			query.setParameter("process", currentProcess);
		}
		if (currentTask != null) {
			query.setParameter("task", currentTask);
		}
		
		List list = query.list();
		return list;
	}
	
	public void selectListener(NodeSelectedEvent event) {
		currentProcess = null;
		currentTask = null;
		HtmlTree tree = (HtmlTree) event.getComponent();
		Object rowData = tree.getRowData();
		if (rowData instanceof ProcessDefinition) {
			currentProcess = (ProcessDefinition) rowData;
		}
		if (rowData instanceof Task) {
			currentTask = (Task) rowData;
		}
	}
		
	public String[] getTaskType() {
		return TASK_TYPES;
	}
}
								