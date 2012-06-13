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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.taskmgmt.exe.TaskInstance;


@SuppressWarnings("unchecked")
@Name("actorTaskList")
@Scope(ScopeType.EVENT)
@BypassInterceptors
public class ActorTaskList {
	
	private Map<String, List<TaskInstance>> tasksForProcess;
	private List<String> processList;

	public List<String> getProcess() {
		tasksForProcess = new HashMap<String, List<TaskInstance>>();
		processList = new ArrayList<String>();
		List<TaskInstance> taskList = getTaskList();
		for (TaskInstance task : taskList) {
			if (task.getProcessInstance().hasEnded()) {
				continue;
			}
			String process = task.getProcessInstance().getProcessDefinition().getName();
			List<TaskInstance> tasks = tasksForProcess.get(process);
			if (tasks == null) {
				tasks = new ArrayList();
				tasksForProcess.put(process, tasks);
				processList.add(process);
			}
			tasks.add(task);
		}
		return processList;
	}
	
	public List<TaskInstance> getTaskList() {
		String actorId = Actor.instance().getId();
		if (actorId == null) {
			return Collections.EMPTY_LIST;
		}
		return ManagedJbpmContext.instance().getTaskList(actorId);
	}
	
	public Map<String, List<TaskInstance>> getTasks(String process) {
		if (tasksForProcess.containsKey(process)) {
			return getMapForTypes(tasksForProcess.get(process));
		}
		return null;
	}

	private Map<String, List<TaskInstance>> getMapForTypes(List<TaskInstance> taskInstances) {
		Map<String, List<TaskInstance>> map = new HashMap<String, List<TaskInstance>>();
		for (TaskInstance task : taskInstances) {
			String name = task.getTask().getName();
			List<TaskInstance> list = map.get(name);
			if (list == null) {
				list = new ArrayList<TaskInstance>();
				map.put(name, list);
			}
			list.add(task);
		}
		return map;
	}
	
	@Transactional
	public List<Entry<String, TaskInstance>> getPooledForType() {
		Actor actor = Actor.instance();
		String actorId = actor.getId();
		if (actorId == null) {
			return null;
		}
		ArrayList groupIds = new ArrayList(actor.getGroupActorIds());
		groupIds.add(actorId);
		List<TaskInstance> taskInstances = ManagedJbpmContext.instance().getGroupTaskList(groupIds);
		return new ArrayList(getMapForTypes(taskInstances).entrySet());
	}

}