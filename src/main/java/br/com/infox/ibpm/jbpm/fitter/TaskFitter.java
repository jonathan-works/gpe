package br.com.infox.ibpm.jbpm.fitter;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;

import org.hibernate.Query;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.ibpm.bean.PrazoTask;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.infox.ibpm.entity.Tarefa;
import br.com.infox.ibpm.home.FluxoHome;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.jbpm.handler.TaskHandler;
import br.com.infox.ibpm.type.PrazoEnum;
import br.com.itx.util.EntityUtil;

@Name(TaskFitter.NAME)
@AutoCreate
public class TaskFitter extends Fitter implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "taskFitter";
	
	private TaskHandler startTaskHandler;
	private TaskHandler currentTask;
	private String taskName;
	private Map<BigInteger, String> modifiedTasks = new HashMap<BigInteger, String>();
	private Integer prazo;
	private PrazoEnum tipoPrazo;
	private Map<String, PrazoTask> prazoTaskMap = new HashMap<String, PrazoTask>();
	
	public void addTask() {
		Node currentNode = pb.getNodeFitter().getCurrentNode();
		ProcessDefinition process = pb.getInstance();
		if (currentNode instanceof TaskNode) {
			getTasks();
			TaskNode tn = (TaskNode) currentNode;
			Task t = new Task();
			t.setProcessDefinition(process);
			t.setTaskMgmtDefinition(process.getTaskMgmtDefinition());
			List<TaskHandler> list = pb.getTaskNodeMap().get(currentNode);
			t.setName(currentNode.getName());
			tn.addTask(t);
			tn.setEndTasks(true);
			t.setSwimlane((Swimlane) process.getTaskMgmtDefinition().getSwimlanes()
					.values().iterator().next());
			TaskHandler th = new TaskHandler(t);
			list.add(th);
			currentTask = th;
		}
	}

	public void removeTask(TaskHandler t) {
		Node currentNode = pb.getNodeFitter().getCurrentNode();
		if (currentNode instanceof TaskNode) {
			TaskNode tn = (TaskNode) currentNode;
			tn.getTasks().remove(t.getTask());
			pb.getTaskNodeMap().remove(currentNode);
		}
	}
	
	public void updatePrazoTask() {
		Fluxo fluxoInstance = FluxoHome.instance().getInstance();
		Set<Entry<String, PrazoTask>> entrySet = prazoTaskMap.entrySet();
		EntityManager entityManager = EntityUtil.getEntityManager();
		for (Entry<String, PrazoTask> entry : entrySet) {
			Tarefa t = JbpmUtil.getTarefa(entry.getKey(),
					fluxoInstance.getFluxo());
			if (t != null) {
				PrazoTask prazoTask = entry.getValue();
				t.setPrazo(prazoTask.getPrazo());
				t.setTipoPrazo(prazoTask.getTipoPrazo());
				entityManager.merge(t);
			}
		}
		entityManager.flush();
	}
	
	public void setPrazo(Integer prazo) {
		this.prazo = prazo;
	}

	public Integer getPrazo() {
		return prazo;
	}

	public void setTipoPrazo(PrazoEnum tipoPrazo) {
		this.tipoPrazo = tipoPrazo;
	}

	public PrazoEnum getTipoPrazo() {
		return tipoPrazo;
	}

	public PrazoEnum[] getTipoPrazoList() {
		return PrazoEnum.values();
	}
	
	public TaskHandler getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(TaskHandler cTask) {
		this.currentTask = cTask;
	}
	
	public void setTaskName(String taskName) {
		if (this.taskName != null && !this.taskName.equals(taskName)) {
			if (currentTask != null && currentTask.getTask() != null) {
				currentTask.getTask().setName(taskName);
				String query = "select max(id_) from jbpm_task where processdefinition_ = "
						+ ":idProcessDefinition and name_ = :taskName";
				List<Object> list = JbpmUtil
						.getJbpmSession()
						.createSQLQuery(query)
						.setParameter("idProcessDefinition",
								pb.getIdProcessDefinition())
						.setParameter("taskName", this.taskName).list();
				if (list != null && list.size() > 0 && list.get(0) != null) {
					modifiedTasks.put((BigInteger) list.get(0), taskName);
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
			Task startTask = pb.getInstance().getTaskMgmtDefinition().getStartTask();
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
		String update;
		Query q;
		if (modifiedTasks.size() > 0) {
			update = "update jbpm_task set name_ = :taskName where id_ = :taskId";
			q = JbpmUtil.getJbpmSession().createSQLQuery(update);
			for (Entry<BigInteger, String> e : modifiedTasks.entrySet()) {
				q.setParameter("taskName", e.getValue());
				q.setParameter("taskId", e.getKey());
				q.executeUpdate();
			}
		}
		JbpmUtil.getJbpmSession().flush();
		modifiedTasks = new HashMap<BigInteger, String>();
	}

	public List<TaskHandler> getTasks() {
		Node currentNode = pb.getNodeFitter().getCurrentNode();
		Map<Node, List<TaskHandler>> taskNodeMap = pb.getTaskNodeMap();
		List<TaskHandler> taskList = new ArrayList<TaskHandler>();
		if (currentNode instanceof TaskNode) {
			TaskNode node = (TaskNode) currentNode;
			if (taskNodeMap == null) {
				pb.setTaskNodeMap(new HashMap<Node, List<TaskHandler>>());
				taskNodeMap = pb.getTaskNodeMap();
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
			Task startTask = pb.getInstance().getTaskMgmtDefinition().getStartTask();
			startTaskHandler = new TaskHandler(startTask);
			taskList.add(startTaskHandler);
			if (! taskList.isEmpty() && currentTask == null) {
				setCurrentTask(taskList.get(0));
			}
		}
		return taskList;
	}
	
	public void setPrazoTasks(Node lastNode, Node cNode) {
		if (cNode == null) {
			prazo = null;
			tipoPrazo = null;
			return;
		}

		PrazoTask prazoTask = new PrazoTask();
		if (lastNode != null && prazo != null && tipoPrazo != null) {
			prazoTask.setPrazo(prazo);
			prazoTask.setTipoPrazo(tipoPrazo);
			prazoTaskMap.put(lastNode.getName(), prazoTask);
		}

		prazoTask = prazoTaskMap.get(cNode.getName());
		if (prazoTask != null) {
			prazo = prazoTask.getPrazo();
			tipoPrazo = prazoTask.getTipoPrazo();
		} else {
			Tarefa t = JbpmUtil.getTarefa(cNode.getName(), FluxoHome.instance()
					.getInstance().getFluxo());
			if (t == null) {
				prazo = null;
				tipoPrazo = null;
			} else {
				prazo = t.getPrazo();
				tipoPrazo = t.getTipoPrazo();
				prazoTask = new PrazoTask();
				prazoTask.setPrazo(prazo);
				prazoTask.setTipoPrazo(tipoPrazo);
				prazoTaskMap.put(cNode.getName(), prazoTask);
			}
		}
	}

	@Override
	public void clear() {
		currentTask = null;
	}

}
