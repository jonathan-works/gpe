package br.com.infox.ibpm.jbpm.fitter;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.def.ExceptionHandler;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.Fork;
import org.jbpm.graph.node.Join;
import org.jbpm.graph.node.NodeTypes;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;

import br.com.infox.ibpm.jbpm.converter.NodeConverter;
import br.com.infox.ibpm.jbpm.handler.NodeHandler;
import br.com.infox.ibpm.jbpm.handler.TaskHandler;
import br.com.infox.ibpm.jbpm.handler.TransitionHandler;
import br.com.infox.ibpm.jbpm.node.MailNode;
import br.com.infox.jbpm.manager.JbpmNodeManager;
import br.com.infox.util.constants.WarningConstants;
import br.com.infox.util.constants.jbpm.NodeTypeConstants;

@Name(NodeFitter.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class NodeFitter extends Fitter implements Serializable {
	
    private static final long serialVersionUID = 1L;
	public static final String NAME = "nodeFitter";
	public static final String SET_CURRENT_NODE_EVENT = "NodeFitter.setCurrentNode";
    private static final LogProvider LOG = Logging.getLogProvider(NodeFitter.class);

	private List<Node> nodes;
	private List<SelectItem> nodesItems;
	private Map<Node, String> nodeMessageMap = new HashMap<Node, String>();
	private Node oldNodeTransition;
	private String newNodeName;
	private String newNodeType = NodeTypeConstants.TASK;
	private Node newNodeAfter;
	private Node currentNode;
	private NodeHandler nodeHandler;
	private String nodeName;
	private Map<BigInteger, String> modifiedNodes = new HashMap<BigInteger, String>();
	
	@In private JbpmNodeManager jbpmNodeManager;
		
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public void addNewNode() {
		Class<?> nodeType = NodeTypes.getNodeType(getNodeType(newNodeType));
		ProcessDefinition processo = getProcessBuilder().getInstance();
		if (nodeType != null) {
			Node node = null;
			try {
				node = (Node) nodeType.newInstance();
			} catch (Exception e) {
			    LOG.error("addNewNode()", e);
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
			TransitionHandler newNodeTransition = getProcessBuilder().getTransitionFitter().getNewNodeTransition();
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
				
				if (oldT.getFrom().getNodeType().equals(NodeType.Fork) && to.getNodeType().equals(NodeType.Join)) {
					getProcessBuilder().getTransitionFitter().connectNodes(oldT.getFrom(), to);
				}
			}
			
			if (nodeType.equals(Fork.class)) {
				handleForkNode(node);
			} else if (nodeType.equals(MailNode.class)) {
				handleMailNode(node);
			}
			
			newNodeName = null;
			newNodeType = null;
			newNodeAfter = null;
			nodesItems = null;
			setCurrentNode(node);
			if (nodeType.equals(TaskNode.class)) {
				getProcessBuilder().getTaskFitter().addTask();
			}
			getProcessBuilder().getTransitionFitter().clearNewNodeTransition();
			getProcessBuilder().getTransitionFitter().clear();
			getProcessBuilder().getTransitionFitter().checkTransitions();
		}
	}
	
	private void handleMailNode(Node node) {
		ExceptionHandler exceptionHandler = new ExceptionHandler();
		exceptionHandler.setExceptionClassName(Throwable.class.getCanonicalName());
		node.addExceptionHandler(exceptionHandler);
	}

	@SuppressWarnings(WarningConstants.UNCHECKED)
	private void handleForkNode(Node fork) {
		try {
			Node join = Join.class.newInstance();
			join.setName(fork.getName() + " (Junção)");
			ProcessDefinition processo = getProcessBuilder().getInstance();
			processo.addNode(join);
			List<Node> nodes = processo.getNodes();
			processo.reorderNode(nodes.indexOf(join), nodes.indexOf(fork) + 1);
			getProcessBuilder().getTransitionFitter().connectNodes(fork, join);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void transitionChangeListener(ValueChangeEvent e) {
		oldNodeTransition = NodeConverter.getAsObject((String) e.getOldValue());
	}

	private void removeTransition(Transition transition) {
	    Node to = transition.getTo();
	    if (to != null) {
	        to.removeArrivingTransition(transition);
	    }
	    Node from = transition.getFrom();
	    if (from != null) {
	        from.removeLeavingTransition(transition);
	    }
	}
	
	private void removeTransitions(Collection<Transition> transitions) {
	    while(transitions.size()>0) {
            Transition t = transitions.iterator().next();
            removeTransition(t);
        }
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public void removeNode(Node node) {
		nodes.remove(node);
		getProcessBuilder().getInstance().removeNode(node);
		if (node.equals(currentNode)) {
			currentNode = null;
		}
		nodeMessageMap.clear();
		List<Transition> transitions = node.getLeavingTransitions();
		if (transitions != null) {
		    removeTransitions(transitions);
		}
		Set<Transition> transitionSet = node.getArrivingTransitions();
		if (transitionSet != null) {
		    removeTransitions(transitionSet);
		}
	}
	
	public void moveUp(Node node) {
		int i = nodes.indexOf(node);
		getProcessBuilder().getInstance().reorderNode(i, i - 1);
		nodes = null;
		nodesItems = null;
	}

	public void moveDown(Node node) {
		int i = nodes.indexOf(node);
		getProcessBuilder().getInstance().reorderNode(i, i + 1);
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
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	@Factory("processNodes")
	public List<Node> getNodes() {
		if (nodes == null) {
			nodes = getProcessBuilder().getInstance().getNodes();
		}
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
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<SelectItem> getNodesItems() {
		if (nodesItems == null) {
			List<Node> list = getProcessBuilder().getInstance().getNodes();
			if (list != null) {
				nodesItems = new ArrayList<SelectItem>();
				nodesItems.add(new SelectItem(null, "Selecione uma tarefa..."));
				for (Node node : list) {
					nodesItems.add(new SelectItem(node.toString(), node.getName()));
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
		return this.newNodeAfter == null ? null : this.newNodeAfter.toString();
	}

	public void setNodeName(String nodeName) {
		if (this.nodeName != null && !this.nodeName.equals(nodeName)) {
			if (currentNode != null) {
				currentNode.setName(nodeName);
				BigInteger idNodeModificado = jbpmNodeManager.findNodeIdByIdProcessDefinitionAndName(getProcessBuilder().getIdProcessDefinition(), nodeName);
				if (idNodeModificado != null) {
					modifiedNodes.put(idNodeModificado, nodeName);
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
		TaskFitter tf = getProcessBuilder().getTaskFitter();
		Node lastNode = this.currentNode;
		this.currentNode = cNode;
		tf.getTasks();
		tf.clear();
		Map<Node, List<TaskHandler>> taskNodeMap = getProcessBuilder().getTaskNodeMap();
		if (taskNodeMap != null && taskNodeMap.containsKey(cNode)) {
			List<TaskHandler> list = taskNodeMap.get(cNode);
			if (!list.isEmpty()) {
				tf.setCurrentTask(list.get(0));
			}
			tf.setPrazoTasks(lastNode, cNode);
		}
		nodeHandler = new NodeHandler(cNode);
		newNodeType = NodeTypeConstants.TASK;
		getProcessBuilder().getTransitionFitter().clearArrivingAndLeavingTransitions();
		getProcessBuilder().getTypeFitter().setTypeList(null);
		if (tf.getCurrentTask() != null) {
			tf.getCurrentTask().clearHasTaskPage();
		}

		Events.instance().raiseEvent(SET_CURRENT_NODE_EVENT);
	}

	public String getNodeForm() {
		String type = "empty";
		if (currentNode != null) {
    		if (NodeType.Decision.equals(currentNode.getNodeType())) {
    			type = "decision";
    		} else if (NodeType.Node.equals(currentNode.getNodeType())) {
    			if (currentNode instanceof ProcessState) {
    				type = "processState";
    			} else if (currentNode instanceof MailNode) {
    				type ="mail";
    			}
    		}
		}
		return type;
	}

	public List<String[]> getNodeTypes() {
		List<String[]> list = new ArrayList<String[]>();
		list.add(new String[] { NodeTypeConstants.START_STATE, "Nó inicial" });
		list.add(new String[] { NodeTypeConstants.TASK, "Tarefa" });
		list.add(new String[] { NodeTypeConstants.DECISION, "Decisão" });
		list.add(new String[] { NodeTypeConstants.MAIL_NODE, "Email" });
		list.add(new String[] { NodeTypeConstants.FORK, "Separação" });
		list.add(new String[] { NodeTypeConstants.JOIN, "Junção" });
		list.add(new String[] { NodeTypeConstants.PROCESS_STATE, "SubProcesso" });
		list.add(new String[] { NodeTypeConstants.NODE, "Sistema" });
		list.add(new String[] { NodeTypeConstants.END_STATE, "Nó Final" });
		return list;
	}

	public String getNodeType(String nodeType) {
		if (nodeType.equals(NodeTypeConstants.TASK)) {
			return "task-node";
		}
		if (nodeType.equals(NodeTypeConstants.MAIL_NODE)) {
			return "mail-node";
		}
		if (nodeType.equals(NodeTypeConstants.DECISION)) {
			return "decision";
		}
		if (nodeType.equals(NodeTypeConstants.START_STATE)) {
			return "start-state";
		}
		if (nodeType.equals(NodeTypeConstants.END_STATE)) {
			return "end-state";
		}
		if (nodeType.equals(NodeTypeConstants.PROCESS_STATE)) {
			return "process-state";
		}
		return nodeType.substring(0, 1).toLowerCase() + nodeType.substring(1);
	}

	public String getIcon(Node node) {
		String icon = node.getNodeType().toString();
		if (node instanceof MailNode) {
			icon = NodeTypeConstants.MAIL_NODE;
		}
		if (node instanceof ProcessState) {
			icon = NodeTypeConstants.PROCESS_STATE;
		}
		return icon;
	}
	
	public void setCurrentNode(TransitionHandler t, String type) {
		if (type.equals("from")) {
			setCurrentNode(t.getTransition().getFrom());
		} else {
			setCurrentNode(t.getTransition().getTo());
		}
	}
	
	public Map<BigInteger, String> getModifiedNodes() {
		return modifiedNodes;
	}

	public void setModifiedNodes(Map<BigInteger, String> modifiedNodes) {
		this.modifiedNodes = modifiedNodes;
	}
	
	public void modifyNodes(){
		jbpmNodeManager.atualizarNodesModificados(modifiedNodes);
		modifiedNodes = new HashMap<BigInteger, String>();
	}

	@Override
	public void clear() {
		currentNode = null;
		nodes = null;
		nodesItems = null;
	}
}
