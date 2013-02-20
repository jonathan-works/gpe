package br.com.infox.ibpm.jbpm.fitter;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.NodeTypes;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;

import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.jbpm.ProcessBuilder;
import br.com.infox.ibpm.jbpm.converter.NodeConverter;
import br.com.infox.ibpm.jbpm.handler.NodeHandler;
import br.com.infox.ibpm.jbpm.handler.TaskHandler;
import br.com.infox.ibpm.jbpm.handler.TransitionHandler;
import br.com.infox.ibpm.jbpm.node.MailNode;
import br.com.itx.util.ComponentUtil;

@Name(NodeFitter.NAME)
@Scope(ScopeType.CONVERSATION)
public class NodeFitter implements Serializable, Fitter{
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "nodeFitter";
	public static final String SET_CURRENT_NODE_EVENT = "NodeFitter.setCurrentNode";

	private List<Node> nodes;
	private List<SelectItem> nodesItems;
	private Map<Node, String> nodeMessageMap = new HashMap<Node, String>();
	private Node oldNodeTransition;
	private String newNodeName;
	private String newNodeType = "Task";
	private Node newNodeAfter;
	private Node currentNode;
	private NodeHandler nodeHandler;
	private String nodeName;
	private Map<BigInteger, String> modifiedNodes = new HashMap<BigInteger, String>();
	
	private ProcessBuilder pb = ComponentUtil.getComponent(ProcessBuilder.NAME);
	
	public void addNewNode() {
		Class<?> nodeType = NodeTypes.getNodeType(getNodeType(newNodeType));
		ProcessDefinition processo = pb.getInstance();
		if (nodeType != null) {
			Node node = null;
			try {
				node = (Node) nodeType.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			node.setName(newNodeName);
			processo.addNode(node);
			nodes = processo.getNodes();
			// Se foi informado newNodeAfter, procura para inserir
			if (newNodeAfter != null) {
				int i = nodes.indexOf(newNodeAfter);
				processo.reorderNode(nodes.indexOf(node), i + 1);
			} else {
				// Senão coloca antes do primeiro EndState
				int i = nodes.size() - 1;
				do {
					i--;
				} while (nodes.get(i) instanceof EndState);
				processo.reorderNode(nodes.indexOf(node), i + 1);
			}
			// insere o novo nó entre os nós da transição selecionada
			// Se for EndState, liga apenas ao newNodeAfter
			TransitionHandler newNodeTransition = pb.getTransitionFitter().getNewNodeTransition();
			if (nodeType.equals(EndState.class)) {
				Transition t = new Transition();
				t.setFrom(newNodeAfter);
				node.addArrivingTransition(t);
				t.setName(node.getName());
				newNodeAfter.addLeavingTransition(t);
			} else if (newNodeTransition != null
					&& newNodeTransition.getTransition() != null) {
				Transition t = new Transition();
				Transition oldT = newNodeTransition.getTransition();
				t.setCondition(oldT.getCondition());
				t.setDescription(oldT.getDescription());
				Node to = newNodeTransition.getTransition().getTo();
				t.setName(to.getName());
				t.setProcessDefinition(oldT.getProcessDefinition());

				to.removeArrivingTransition(oldT);
				to.addArrivingTransition(t);

				node.addLeavingTransition(t);
				newNodeTransition.setName(node.getName());
				node.addArrivingTransition(oldT);
			}
			
			newNodeName = null;
			newNodeType = null;
			newNodeAfter = null;
			nodesItems = null;
			setCurrentNode(node);
			if (nodeType.equals(TaskNode.class)) {
				pb.getTaskFitter().addTask();
			}
			pb.getTransitionFitter().clearNewNodeTransition();
			pb.getTransitionFitter().clear();
			pb.getTransitionFitter().checkTransitions();
		}
	}
	
	public void transitionChangeListener(ValueChangeEvent e) {
		oldNodeTransition = NodeConverter.getAsObject((String) e.getOldValue());
	}

	public void removeNode(Node node) {
		nodes.remove(node);
		pb.getInstance().removeNode(node);
		if (node.equals(currentNode)) {
			currentNode = null;
		}
		nodeMessageMap.clear();
		for (Node n : nodes) {
			List<Transition> transitions = n.getLeavingTransitions();
			if (transitions != null) {
				for (Iterator<Transition> i = transitions.iterator(); i
						.hasNext();) {
					Transition t = i.next();
					if (t.getTo().equals(node)) {
						i.remove();
					}
				}
			}
			Set<Transition> transitionSet = n.getArrivingTransitions();
			if (transitionSet != null) {
				for (Iterator<Transition> i = transitionSet.iterator(); i
						.hasNext();) {
					Transition t = i.next();
					if (t.getFrom().equals(node)) {
						i.remove();
					}
				}
			}
		}
		pb.getTransitionFitter().checkTransitions();
	}
	
	public void moveUp(Node node) {
		int i = nodes.indexOf(node);
		pb.getInstance().reorderNode(i, i - 1);
		nodes = null;
		nodesItems = null;
	}

	public void moveDown(Node node) {
		int i = nodes.indexOf(node);
		pb.getInstance().reorderNode(i, i + 1);
		nodes = null;
		nodesItems = null;
	}
	
	public NodeHandler getNodeHandler() {
		return nodeHandler;
	}

	public Integer getNodeIndex() {
		return null;
	}

	public void setNodeIndex(Integer i) {
		setCurrentNode(getNodes().get(i));
	}
	
	public String getNewNodeName() {
		return newNodeName;
	}

	public void setNewNodeName(String newName) {
		this.newNodeName = newName;
	}

	public String getNewNodeType() {
		return newNodeType;
	}

	public void setNewNodeType(String newNodeType) {
		this.newNodeType = newNodeType;
	}
	
	public Map<Node, String> getNodeMessageMap() {
		return nodeMessageMap;
	}

	public void setNodeMessageMap(Map<Node, String> nodeMessageMap) {
		this.nodeMessageMap = nodeMessageMap;
	}

	public String getMessage(Node n) {
		return nodeMessageMap.get(n);
	}
	
	public Node getOldNodeTransition() {
		return oldNodeTransition;
	}
	
	@Factory("processNodes")
	public List<Node> getNodes() {
		if (nodes == null) {
			nodes = new ArrayList<Node>();
			List<Node> list = pb.getInstance().getNodes();
			if (list != null) {
				for (Node node : list) {
					nodes.add(node);
				}
			}
		}
		//getTransitionFitter().checkTransitions();
		return nodes;
	}
	
	public List<Node> getNodes(String type) {
		List<Node> nodeList = new ArrayList<Node>(nodes);
		for (Iterator<Node> iterator = nodeList.iterator(); iterator.hasNext();) {
			Node n = iterator.next();
			if (type.equals("from") && (n instanceof EndState)) {
				iterator.remove();
			}
			if (type.equals("to") && (n instanceof StartState)) {
				iterator.remove();
			}
		}
		return nodeList;
	}
	
	public List<SelectItem> getNodesItems() {
		if (nodesItems == null) {
			List<Node> list = pb.getInstance().getNodes();
			if (list != null) {
				nodesItems = new ArrayList<SelectItem>();
				nodesItems.add(new SelectItem(null, "Selecione uma tarefa..."));
				for (Node node : list) {
					nodesItems.add(new SelectItem(node.toString(), node
							.getName()));
				}
			}
		}
		return nodesItems;
	}
	
	public void setNodesItems(List<SelectItem> nodesList) {
		this.nodesItems = nodesList;
	}

	public List<SelectItem> getNodesTransitionItems(String type) {
		List<SelectItem> nodeItemsList = new ArrayList<SelectItem>();
		nodeItemsList.add(new SelectItem(null, "Selecione..."));
		for (Node node : getNodes(type)) {
			nodeItemsList.add(new SelectItem(node, node.getName()));
		}
		return nodeItemsList;
	}

	public void setNewNodeAfter(String newNodeAfter) {
		for (Node node : getNodes()) {
			if (node.toString().equals(newNodeAfter)) {
				this.newNodeAfter = node;
			}
		}
	}
	
	public String getNewNodeAfter() {
		return null;
	}

	public void setNodeName(String nodeName) {
		if (this.nodeName != null && !this.nodeName.equals(nodeName)) {
			if (currentNode != null) {
				currentNode.setName(nodeName);
				String query = "select max(id_) from jbpm_node where processdefinition_ = "
						+ ":idProcessDefinition and name_ = :nodeName";
				SQLQuery sql = JbpmUtil.getJbpmSession().createSQLQuery(query);
				Query param = sql.setParameter("idProcessDefinition",
						pb.getIdProcessDefinition()).setParameter("nodeName",
						nodeName);
				List<Object> list = param.list();
				if (list != null && list.size() > 0 && list.get(0) != null) {
					modifiedNodes.put((BigInteger) list.get(0), nodeName);
				}
			}
			this.nodeName = nodeName;
		}
	}

	public String getNodeName() {
		if (currentNode != null) {
			nodeName = currentNode.getName();
		}
		return nodeName;
	}

	public Node getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(Node cNode) {
		TaskFitter tf = pb.getTaskFitter();
		Node lastNode = this.currentNode;
		this.currentNode = cNode;
		tf.getTasks();
		tf.clear();
		Map<Node, List<TaskHandler>> taskNodeMap = pb.getTaskNodeMap();
		if (taskNodeMap != null && taskNodeMap.containsKey(cNode)) {
			List<TaskHandler> list = taskNodeMap.get(cNode);
			if (!list.isEmpty()) {
				tf.setCurrentTask(list.get(0));
			}
			tf.setPrazoTasks(lastNode, cNode);
		}
		nodeHandler = new NodeHandler(cNode);
		newNodeType = "Task";
		pb.getTransitionFitter().clearArrivingAndLeavingTransitions();
		pb.getTypeFitter().setTypeList(null);
		if (tf.getCurrentTask() != null) {
			tf.getCurrentTask().clearHasTaskPage();
		}

		Events.instance().raiseEvent(SET_CURRENT_NODE_EVENT);
	}

	public String getNodeForm() {
		String type = null;
		if (currentNode == null) {
			return type;
		}
		if (currentNode.getNodeType().equals(NodeType.Decision)) {
			type = "decision";
		} else if (!currentNode.getNodeType().equals(NodeType.StartState)) {
			if (currentNode instanceof MailNode) {
				type = "mail";
			}
			if (currentNode instanceof ProcessState) {
				type = "processState";
			}
		}
		return type;
	}

	public List<String[]> getNodeTypes() {
		List<String[]> list = new ArrayList<String[]>();
		list.add(new String[] { "StartState", "Nó inicial" });
		list.add(new String[] { "Task", "Tarefa" });
		list.add(new String[] { "Decision", "Decisão" });
		list.add(new String[] { "MailNode", "Email" });
		list.add(new String[] { "Fork", "Separação" });
		list.add(new String[] { "Join", "Junção" });
		list.add(new String[] { "ProcessState", "SubProcesso" });
		list.add(new String[] { "Node", "Sistema" });
		list.add(new String[] { "EndState", "Nó Final" });
		return list;
	}

	public String getNodeType(String nodeType) {
		if (nodeType.equals("Task")) {
			return "task-node";
		}
		if (nodeType.equals("MailNode")) {
			return "mail-node";
		}
		if (nodeType.equals("Decision")) {
			return "decision";
		}
		if (nodeType.equals("StartState")) {
			return "start-state";
		}
		if (nodeType.equals("EndState")) {
			return "end-state";
		}
		if (nodeType.equals("ProcessState")) {
			return "process-state";
		}
		return nodeType.substring(0, 1).toLowerCase() + nodeType.substring(1);
	}

	public String getIcon(Node node) {
		String icon = node.getNodeType().toString();
		if (node instanceof MailNode) {
			icon = "MailNode";
		}
		if (node instanceof ProcessState) {
			icon = "ProcessState";
		}
		return icon;
	}
	
	public void setCurrentNode(Transition t, String type) {
		if (type.equals("from")) {
			setCurrentNode(t.getFrom());
		} else {
			setCurrentNode(t.getTo());
		}
	}
	
	public Map<BigInteger, String> getModifiedNodes() {
		return modifiedNodes;
	}

	public void setModifiedNodes(Map<BigInteger, String> modifiedNodes) {
		this.modifiedNodes = modifiedNodes;
	}
	
	public void modifyNodes(){
		String update;
		Query q;
		if (modifiedNodes.size() > 0) {
			update = "update jbpm_node set name_ = :nodeName where id_ = :nodeId";
			q = JbpmUtil.getJbpmSession().createSQLQuery(update);
			for (Entry<BigInteger, String> e : modifiedNodes.entrySet()) {
				q.setParameter("nodeName", e.getValue());
				q.setParameter("nodeId", e.getKey());
				q.executeUpdate();
			}
		}
		JbpmUtil.getJbpmSession().flush();
		modifiedNodes = new HashMap<BigInteger, String>();
	}

	@Override
	public void clear() {
		currentNode = null;
		nodes = null;
		nodesItems = null;
	}
}
