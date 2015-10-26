package br.com.infox.ibpm.process.definition.fitter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.instantiation.Delegation;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.processo.timer.TaskExpiration;
import br.com.infox.epp.processo.timer.manager.TaskExpirationManager;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.ibpm.listener.EppJbpmListener;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.handler.InfoxTaskControllerHandler;
import br.com.infox.ibpm.task.handler.TaskHandler;
import br.com.infox.ibpm.task.manager.JbpmTaskManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@AutoCreate
@ContextDependency
@Name(TaskFitter.NAME)
public class TaskFitter extends Fitter implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(TaskFitter.class);
    public static final String NAME = "taskFitter";

    private TaskHandler startTaskHandler;
    private TaskHandler currentTask;
    private String taskName;
    private Map<Number, String> modifiedTasks = new HashMap<>();
    private Tarefa tarefaAtual;
    private Set<Tarefa> tarefasModificadas = new HashSet<>();
    private boolean currentJbpmTaskPersisted;
    private List<VariableType> typeList;
    private TaskExpiration taskExpiration;

    @In
    private JbpmTaskManager jbpmTaskManager;
    @In
    private TarefaManager tarefaManager;
    @In
    private GenericManager genericManager;
    @In
    private TaskExpirationManager taskExpirationManager;
    @Inject
    private EppJbpmListener eppJbpmListener;
    @Inject 
    private InfoxMessages infoxMessages;
 
    public void addTask() {
        Node currentNode = getProcessBuilder().getNodeFitter().getCurrentNode();
        ProcessDefinition process = getProcessBuilder().getInstance();
        if (currentNode instanceof TaskNode) {
            getTasks();
            TaskNode taskNode = (TaskNode) currentNode;
            Task task = new Task();
            task.setKey(UUID.randomUUID().toString());
            task.setProcessDefinition(process);
            task.setTaskMgmtDefinition(process.getTaskMgmtDefinition());
            List<TaskHandler> list = getProcessBuilder().getTaskNodeMap().get(currentNode);
            task.setName(currentNode.getName());
            taskNode.addTask(task);
            taskNode.setEndTasks(true);
            task.setSwimlane((Swimlane) process.getTaskMgmtDefinition().getSwimlanes().values().iterator().next());
            task.setTaskController(new TaskController());
            task.getTaskController().setVariableAccesses(new ArrayList<>());
            Delegation delegation = new Delegation(InfoxTaskControllerHandler.class.getName());
            delegation.setProcessDefinition(task.getProcessDefinition());
            task.getTaskController().setTaskControllerDelegation(delegation);
            TaskHandler th = new TaskHandler(task);
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
        this.taskExpiration = null;
        checkCurrentTaskPersistenceState();
    }

    public Tarefa getTarefaAtual() {
        if (this.tarefaAtual == null && getCurrentTask() != null
                && isCurrentJbpmTaskPersisted()) {
            this.tarefaAtual = tarefaManager.getTarefa(getTaskId(getProcessBuilder().getIdProcessDefinition(), getTaskName()).longValue());
        }
        return tarefaAtual;
    }

    public void setTaskName(String taskName) {
        if (this.taskName != null && !this.taskName.equals(taskName)) {
            if (currentTask != null && currentTask.getTask() != null) {
                currentTask.getTask().setName(taskName);
                Number idTaskModificada = getTaskId(getProcessBuilder().getIdProcessDefinition(), getTaskName());
                if (idTaskModificada != null) {
                    modifiedTasks.put(idTaskModificada, taskName);
                }
            }
            if (taskExpiration != null && taskExpiration.getId() != null) {
                taskExpiration.setTarefa(taskName);
                try {
                    taskExpirationManager.update(taskExpiration);
                } catch (DAOException e) {
                    LOG.error("taskFitter.setTaskName", e);
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

    public void setStarTaskHandler(TaskHandler startTask) {
        startTaskHandler = startTask;
    }

    public Map<Number, String> getModifiedTasks() {
        return modifiedTasks;
    }

    public void setModifiedTasks(Map<Number, String> modifiedTasks) {
        this.modifiedTasks = modifiedTasks;
    }

    public void modifyTasks() {
        jbpmTaskManager.atualizarTarefasModificadas(modifiedTasks);
        modifiedTasks = new HashMap<Number, String>();
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
            if (!taskList.isEmpty() && currentTask == null) {
                setCurrentTask(taskList.get(0));
            }
        } else if (currentNode instanceof StartState) {
            Task startTask = getProcessBuilder().getInstance().getTaskMgmtDefinition().getStartTask();
            startTaskHandler = new TaskHandler(startTask);
            taskList.add(startTaskHandler);
            if (!taskList.isEmpty() && currentTask == null) {
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
        tarefaManager.flush();
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
    
    public boolean canChangeCurrentTaskName() {
        return currentTask != null && !getProcessBuilder().existemProcessosAssociadosAoFluxo();
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

    public TaskExpiration getTaskExpiration() {
        if (taskExpiration == null) {
            setTaskExpiration(new TaskExpiration());
        }
        return taskExpiration;
    }

    public void setTaskExpiration(TaskExpiration taskExpiration) {
        this.taskExpiration = taskExpiration;
    }
    
    public void addExpiration() {
        ProcessBuilder processBuilder = ProcessBuilder.instance();
        taskExpiration.setFluxo(processBuilder.getFluxo());
        taskExpiration.setTarefa(getTaskName());
        if (taskExpiration.getExpiration() != null && taskExpiration.getTransition() != null) {
            try {
                taskExpirationManager.persist(taskExpiration);
            } catch (DAOException e) {
                LOG.error("taskFitter.addExpiration()", e);
            }
        }
    }
    
    public void removeExpiration(TaskExpiration te) {
        try {
            taskExpirationManager.remove(te);
            setTaskExpiration(new TaskExpiration());
        } catch (DAOException e) {
            LOG.error("taskFitter.removeExpiration()", e);
        }
    }
    
    public boolean hasTaskExpiration() {
        if (taskExpiration == null) {
            TaskExpiration te = taskExpirationManager.getByFluxoAndTaskName(ProcessBuilder.instance().getFluxo(), taskName);
            taskExpiration = te == null ? new TaskExpiration() : te;
        }
        return taskExpiration != null && taskExpiration.getId() != null;
    }
    
    public List<SelectItem> getListenersDisponiveis() {
    	List<SelectItem> listenersDisponiveis = new ArrayList<>();
    	if (currentTask != null) {
        	for (String key : eppJbpmListener.getListeners().keySet()) {
        		if (currentTask.getTask().getEvents() == null || !currentTask.getTask().getEvents().containsKey(key)) {
        			listenersDisponiveis.add(new SelectItem(key, eppJbpmListener.getListeners().get(key)));
        		}
        	}
    	}
    	return listenersDisponiveis;
    }
    
    public String getListenerLabel(Event event) {
    	return eppJbpmListener.getListeners().get(event.getEventType());
    }
    
    public String getListenerConfiguration(Event event) {
    	if (event.getConfiguration() != null) {
    		JsonObject jsonObject = new GsonBuilder().create().fromJson(event.getConfiguration(), JsonObject.class);
    		String key = jsonObject.get("transitionKey").getAsString();
    		StringBuilder sb = new StringBuilder();
    		if (key != null) {
    			sb.append(infoxMessages.get("process.expiration.transition")).append(": ").append(currentTask.getTask().getTaskNode().getLeavingTransition(key).getName());
    		}
    		return sb.toString();
    	}
    	return "";
    }
    
	public Collection<Event> getListeners() {
		List<Event> listeners = new ArrayList<>();
    	if (currentTask != null && currentTask.getTask().getEvents() != null) {
    		for (Event event : currentTask.getTask().getEvents().values()) {
    			if (event.getEventType().startsWith(Event.EVENTTYPE_TASK_LISTENER)) {
    				listeners.add(event);
    			}
    		}
    	}
    	return listeners;
    }
    
	public void addListener(ActionEvent actionEvent) {
    	Map<String, String> request = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
    	String inputNome = (String) actionEvent.getComponent().getAttributes().get("listenerValue");
    	String inputTransicao = (String) actionEvent.getComponent().getAttributes().get("transitionValue");
    	String nome = request.get(inputNome);
    	String transicao = request.get(inputTransicao);
		Event eventRedistribuicao = new Event(nome);
		if (transicao != null) {
			Transition transition = currentTask.getTask().getTaskNode().getLeavingTransition(transicao);
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("transitionKey", transition.getKey());
			eventRedistribuicao.setConfiguration(jsonObject.toString());
		}
		currentTask.getTask().addEvent(eventRedistribuicao);
    }
    
    public void removeListener(Event event) {
    	currentTask.getTask().removeEvent(event);
    }
    
}