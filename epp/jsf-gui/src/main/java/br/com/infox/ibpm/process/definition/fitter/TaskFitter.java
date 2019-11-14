package br.com.infox.ibpm.process.definition.fitter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.instantiation.Delegation;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.handler.InfoxTaskControllerHandler;
import br.com.infox.ibpm.task.handler.TaskHandler;
import br.com.infox.ibpm.task.manager.JbpmTaskManager;
import br.com.infox.ibpm.util.BpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named
@ViewScoped
public class TaskFitter extends Fitter implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(TaskFitter.class);

    private TaskHandler startTaskHandler;
    private TaskHandler currentTask;
    private String taskName;
    private Tarefa tarefaAtual;
    private Set<Tarefa> tarefasModificadas = new HashSet<>();
    private boolean currentJbpmTaskPersisted;
    private List<VariableType> typeList;
    private Map<String, List<TaskHandler>> taskNodeMap = new HashMap<>();

    @Inject
    private JbpmTaskManager jbpmTaskManager;
    @Inject
    private TarefaManager tarefaManager;
    
    public void addStartStateTask() {
        StartState startState = (StartState) getProcessBuilder().getNodeFitter().getCurrentNode();
        ProcessDefinition processDefinition = getProcessBuilder().getInstance();
        getTasks();
        Task startTask = new Task();
        startTask.setKey(BpmUtil.generateKey());
        startTask.setProcessDefinition(processDefinition);
        startTask.setTaskMgmtDefinition(processDefinition.getTaskMgmtDefinition());
        startTask.setName(startState.getName());
        startTask.setStartState(startState);
        startTask.setTaskController(new TaskController());
        startTask.getTaskController().setVariableAccesses(new ArrayList<VariableAccess>());
        Delegation delegation = new Delegation(InfoxTaskControllerHandler.class.getName());
        delegation.setProcessDefinition(startTask.getProcessDefinition());
        startTask.getTaskController().setTaskControllerDelegation(delegation);
        TaskHandler taskHandler = new TaskHandler(startTask);
        setCurrentTask(taskHandler);
        List<TaskHandler> list = taskNodeMap.get(startState);
        if (list == null) {
        	list = new ArrayList<>();
        	taskNodeMap.put(startState.getKey(), list);
        }
        list.add(taskHandler);
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
        if (this.tarefaAtual == null && getCurrentTask() != null
                && isCurrentJbpmTaskPersisted()) {
            this.tarefaAtual = tarefaManager.getTarefa(getTaskId(getProcessBuilder().getIdProcessDefinition(), getTaskName()).longValue());
        }
        return tarefaAtual;
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

    public void setStarTaskHandler(TaskHandler startTask) {
        startTaskHandler = startTask;
    }

    public List<TaskHandler> getTasks() {
        Node currentNode = getProcessBuilder().getNodeFitter().getCurrentNode();
        List<TaskHandler> taskList = new ArrayList<TaskHandler>();
        if (currentNode instanceof TaskNode) {
            TaskNode node = (TaskNode) currentNode;
            taskList = taskNodeMap.get(node.getKey());
            if (taskList == null) {
                taskList = TaskHandler.createList(node);
                taskNodeMap.put(node.getKey(), taskList);
            }
            if (!taskList.isEmpty()) {
                setCurrentTask(taskList.get(0));
            }
        } else if (currentNode instanceof StartState) {
            Task startTask = getProcessBuilder().getInstance().getTaskMgmtDefinition().getStartTask();
            startTaskHandler = new TaskHandler(startTask);
            taskList.add(startTaskHandler);
            if (!taskList.isEmpty()) {
                setCurrentTask(taskList.get(0));
            }
        }
        return taskList;
    }

    @Override
    public void clear() {
        setCurrentTask(null);
        taskNodeMap = new HashMap<>();
    }

    public void marcarTarefaAtual() {
        if (!tarefasModificadas.contains(getTarefaAtual())) {
            tarefasModificadas.add(tarefaAtual);
        }
    }
    
    public Set<Tarefa> getTarefasModificadas() {
		return tarefasModificadas;
	}

    public boolean isCurrentJbpmTaskPersisted() {
        return currentJbpmTaskPersisted;
    }

    public void checkCurrentTaskPersistenceState() {
        Number idProcessDefinition = getProcessBuilder().getIdProcessDefinition();
        String currentTaskName = getTaskName();
        this.currentJbpmTaskPersisted = getTaskId(idProcessDefinition, currentTaskName) != null;
    }

    private Number getTaskId(Number idProcessDefinition, String taskName) {
        if (idProcessDefinition != null && taskName != null) {
            return jbpmTaskManager.findTaskIdByIdProcessDefinitionAndName(idProcessDefinition, taskName);
        }
        return null;
    }
    
    public List<VariableType> getTypeList() {
        if (typeList == null) {
            typeList = Arrays.asList(VariableType.values());
        }
        return typeList;
    }

    public void setTypeList(List<VariableType> typeList) {
        this.typeList = typeList;
    }

}