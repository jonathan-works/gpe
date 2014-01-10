package br.com.infox.ibpm.process.definition.fitter;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.ibpm.task.handler.TaskHandler;
import br.com.infox.ibpm.task.manager.JbpmTaskManager;
import br.com.itx.util.EntityUtil;

@Name(TaskFitter.NAME)
@AutoCreate
public class TaskFitter extends Fitter implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(TaskFitter.class);
	public static final String NAME = "taskFitter";
	
	private TaskHandler startTaskHandler;
	private TaskHandler currentTask;
	private String taskName;
	private Map<BigInteger, String> modifiedTasks = new HashMap<BigInteger, String>();
	private Tarefa tarefaAtual;
	private Set<Tarefa> tarefasModificadas = new HashSet<>();
	private boolean currentJbpmTaskPersisted;
	
	@In private JbpmTaskManager jbpmTaskManager;
	@In private TarefaManager tarefaManager;
	
	public void addTask() {
		Node currentNode = getProcessBuilder().getNodeFitter().getCurrentNode();
		ProcessDefinition process = getProcessBuilder().getInstance();
		if (currentNode instanceof TaskNode) {
			getTasks();
			TaskNode tn = (TaskNode) currentNode;
			Task t = new Task();
			t.setProcessDefinition(process);
			t.setTaskMgmtDefinition(process.getTaskMgmtDefinition());
			List<TaskHandler> list = getProcessBuilder().getTaskNodeMap().get(currentNode);
			t.setName(currentNode.getName());
			tn.addTask(t);
			tn.setEndTasks(true);
			t.setSwimlane((Swimlane) process.getTaskMgmtDefinition().getSwimlanes()
					.values().iterator().next());
			TaskHandler th = new TaskHandler(t);
			list.add(th);
			setCurrentTask(th);
		}
	}

	public void removeTask(TaskHandler t) {
		Node currentNode = getProcessBuilder().getNodeFitter().getCurrentNode();
		if (currentNode instanceof TaskNode) {
			TaskNode tn = (TaskNode) currentNode;
			tn.getTasks().remove(t.getTask());
			getProcessBuilder().getTaskNodeMap().remove(currentNode);
		}
		
		if (currentTask != null && currentTask.equals(t)) {
			clear();
		}
	}

	public TaskHandler getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(TaskHandler cTask) {
		this.currentTask = cTask;
		this.tarefaAtual = null;
		checkCurrentTaskPersistenceState();
	}
	
	public Tarefa getTarefaAtual() {
		if (this.tarefaAtual == null && getCurrentTask() != null && isCurrentJbpmTaskPersisted()) {
			this.tarefaAtual = tarefaManager.getTarefa(getTaskId(getProcessBuilder().getIdProcessDefinition(), getTaskName()).longValue());
		}
		return tarefaAtual;
	}
	
	public void setTaskName(String taskName) {
		if (this.taskName != null && !this.taskName.equals(taskName)) {
			if (currentTask != null && currentTask.getTask() != null) {
				currentTask.getTask().setName(taskName);
				BigInteger idTaskModificada = getTaskId(getProcessBuilder().getIdProcessDefinition(), getTaskName());
				if (idTaskModificada != null) {
					modifiedTasks.put(idTaskModificada, taskName);
				}
			}
			this.taskName = taskName;
		}
	}
	
	public String getTaskName() {
		if (currentTask != null && currentTask.getTask() != null) {
			taskName = currentTask.getTask().getName();
		}
		return taskName;
	}
	
	public TaskHandler getStartTaskHandler() {
		if (startTaskHandler == null) {
			Task startTask = getProcessBuilder().getInstance().getTaskMgmtDefinition().getStartTask();
			startTaskHandler = new TaskHandler(startTask);
		}
		return startTaskHandler;
	}
	
	public void setStarTaskHandler(TaskHandler startTask){
		startTaskHandler = startTask;
	}
	
	public Map<BigInteger, String> getModifiedTasks() {
		return modifiedTasks;
	}

	public void setModifiedTasks(Map<BigInteger, String> modifiedTasks) {
		this.modifiedTasks = modifiedTasks;
	}
	
	public void modifyTasks(){
		jbpmTaskManager.atualizarTarefasModificadas(modifiedTasks);
		modifiedTasks = new HashMap<BigInteger, String>();
	}

	public List<TaskHandler> getTasks() {
		Node currentNode = getProcessBuilder().getNodeFitter().getCurrentNode();
		Map<Node, List<TaskHandler>> taskNodeMap = getProcessBuilder().getTaskNodeMap();
		List<TaskHandler> taskList = new ArrayList<TaskHandler>();
		if (currentNode instanceof TaskNode) {
			TaskNode node = (TaskNode) currentNode;
			if (taskNodeMap == null) {
				getProcessBuilder().setTaskNodeMap(new HashMap<Node, List<TaskHandler>>());
				taskNodeMap = getProcessBuilder().getTaskNodeMap();
			}
			taskList = taskNodeMap.get(node);
			if (taskList == null) {
				taskList = TaskHandler.createList(node);
				taskNodeMap.put(node, taskList);
			}
			if (! taskList.isEmpty() && currentTask == null) {
				setCurrentTask(taskList.get(0));
			}
		} else if (currentNode instanceof StartState) {
			Task startTask = getProcessBuilder().getInstance().getTaskMgmtDefinition().getStartTask();
			startTaskHandler = new TaskHandler(startTask);
			taskList.add(startTaskHandler);
			if (! taskList.isEmpty() && currentTask == null) {
				setCurrentTask(taskList.get(0));
			}
		}
		return taskList;
	}

	@Override
	public void clear() {
		setCurrentTask(null);
	}

	public void marcarTarefaAtual() {
		if (!tarefasModificadas.contains(getTarefaAtual())) {
			tarefasModificadas.add(tarefaAtual);
		}
	}
	
	public void updateTarefas() {
		for (Tarefa tarefa : tarefasModificadas) {
			try {
                tarefaManager.merge(tarefa);
            } catch (DAOException e) {
                LOG.error("Erro ao dar merge na tarefa " + tarefa, e);
            }
		}
		EntityUtil.flush();
	}
	
	public boolean isCurrentJbpmTaskPersisted() {
		return currentJbpmTaskPersisted;
	}
	
	public void checkCurrentTaskPersistenceState() {
		BigInteger idProcessDefinition = getProcessBuilder().getIdProcessDefinition();
		String currentTaskName = getTaskName();
		this.currentJbpmTaskPersisted = getTaskId(idProcessDefinition, currentTaskName) != null;
	}
	
	private BigInteger getTaskId(BigInteger idProcessDefinition, String taskName) {
		if (idProcessDefinition != null && taskName != null) {
			return jbpmTaskManager.findTaskIdByIdProcessDefinitionAndName(idProcessDefinition, taskName);
		}
		return null;
	}
}
