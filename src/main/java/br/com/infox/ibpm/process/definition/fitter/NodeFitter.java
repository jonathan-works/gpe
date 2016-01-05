package br.com.infox.ibpm.process.definition.fitter;

import static br.com.infox.core.comparators.Comparators.bySelectItemLabelAsc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.el.ELException;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.el.parser.ELParser;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jbpm.graph.def.Event;
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

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.fluxo.merger.service.FluxoMergeService;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.manager.StatusProcessoManager;
import br.com.infox.ibpm.node.DecisionNode;
import br.com.infox.ibpm.node.InfoxMailNode;
import br.com.infox.ibpm.node.constants.NodeTypeConstants;
import br.com.infox.ibpm.node.handler.NodeHandler;
import br.com.infox.ibpm.node.manager.JbpmNodeManager;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.sinal.Signal;
import br.com.infox.ibpm.sinal.SignalConfigurationBean;
import br.com.infox.ibpm.sinal.SignalDao;
import br.com.infox.ibpm.task.handler.TaskHandler;
import br.com.infox.ibpm.transition.TransitionHandler;
import br.com.infox.jsf.util.JsfUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Named
@ViewScoped
public class NodeFitter extends Fitter implements Serializable {

    private static final long serialVersionUID = 1L;
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
    private Map<Number, String> modifiedNodes = new HashMap<Number, String>();
    private List<ClassificacaoDocumento> classificacoesDocumento;
    private List<Signal> signals;

    @Inject
    private JbpmNodeManager jbpmNodeManager;
    @Inject
    private TransitionFitter transitionFitter;
    @Inject
    private StatusProcessoManager statusProcessoManager;
    @Inject
    private ClassificacaoDocumentoFacade classificacaoDocumentoFacade;
    @Inject
    private InfoxMessages infoxMessages;
    @Inject
    private FluxoMergeService fluxoMergeService;
    @Inject
    private SignalDao signalDao; 
    
    @PostConstruct
    private void init() {
        signals = signalDao.findAll();
    }
    
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
            if (nodeType.equals(InfoxMailNode.class) || nodeType.equals(Node.class)){
                node.setAsync(true);
            }
            node.setName(newNodeName);
            node.setKey(UUID.randomUUID().toString());
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
                t.setKey(UUID.randomUUID().toString());
                newNodeAfter.addLeavingTransition(t);
            } else if (newNodeTransition != null && newNodeTransition.getTransition() != null) {
                Transition t = new Transition();
                Transition oldT = newNodeTransition.getTransition();
                t.setCondition(oldT.getCondition());
                t.setDescription(oldT.getDescription());
                Node to = newNodeTransition.getTransition().getTo();
                t.setName(to.getName());
                t.setKey(UUID.randomUUID().toString());
                t.setProcessDefinition(oldT.getProcessDefinition());

                /*
                 * Não reordenar as linhas de código marcadas pelo bloco abaixo
                 * (ver tarefa #35099) A alteração dos atributos from e name da
                 * transição altera seu hashcode causando erros
                 */
                // INÍCIO BLOCO //
                node.addLeavingTransition(t);
                to.removeArrivingTransition(oldT);
                newNodeTransition.setName(node.getName());
                node.addArrivingTransition(oldT);
                to.addArrivingTransition(t);
                // FIM BLOCO //
            }

            if (nodeType.equals(Fork.class)) {
                handleForkNode(node);
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

    public List<ModeloDocumento> getModeloDocumentoList() {
        return ((ModeloDocumentoManager) ComponentUtil.getComponent(ModeloDocumentoManager.NAME)).getModeloDocumentoList();
    }

    private void handleForkNode(Node fork) {
        try {
            Node join = Join.class.newInstance();
            join.setName(fork.getName() + " (Junção)");
            ProcessDefinition processo = getProcessBuilder().getInstance();
            processo.addNode(join);
            List<Node> nodes = processo.getNodes();
            processo.reorderNode(nodes.indexOf(join), nodes.indexOf(fork) + 1);
            
            Transition t = (Transition) fork.getLeavingTransitions().get(0);
            fork.removeLeavingTransition(t);
            join.addLeavingTransition(t);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void transitionChangeListener(ValueChangeEvent e) {
    	oldNodeTransition = getNodeByName((String) e.getOldValue());
    }

    private void removeTransition(Transition transition) {
        Node to = transition.getTo();
        if (to != null) {
            to.removeArrivingTransition(transition);
        }
        Node from = transition.getFrom();
        if (from != null) {
            removeListener(from, transition);
            from.removeLeavingTransition(transition);
        }
    }
    
    public void removeListener(Node node, Transition transition) {
        Map<String, Event> events = node.getEvents();
        if (events == null) return;
        List<Event> removeEvents = new ArrayList<>();
        for (Event event : events.values()) {
            if (event.isListener() && event.getConfiguration().contains(transition.getKey())) {
                removeEvents.add(event);
            }
        }
        for (Event event : removeEvents) {
            node.removeEvent(event);
        }
    }

    private void removeTransitions(Collection<Transition> transitions) {
        while (transitions.size() > 0) {
            Transition t = transitions.iterator().next();
            removeTransition(t);
        }
    }

    public void removeNode(Node node) {
        if (fluxoMergeService.hasActiveNode(ProcessBuilder.instance().getInstance(), node)) {
            FacesMessages.instance().clear();
            FacesMessages.instance().add("Esta ação não pode ser executada enquanto o nó possuir atividade em fluxo instanciado");
            return;
        }
        nodes.remove(node);
        getProcessBuilder().getInstance().removeNode(node);
        if (node.equals(currentNode)) {
            currentNode = null;
        }
        nodeMessageMap.clear();
        List<Transition> transitions = node.getLeavingTransitions();
        Node join = null;
        if (transitions != null) {
            if (node.getNodeType().equals(NodeType.Fork)) {
                for (Transition t : transitions) {
                    if (t.getTo().getNodeType().equals(NodeType.Join)) {
                        join = t.getTo();
                        break;
                    }
                }
            }
            removeTransitions(transitions);
        }
        Collection<Transition> transitionSet = node.getArrivingTransitions();
        if (transitionSet != null) {
            removeTransitions(transitionSet);
        }
        if (join != null) {
            removeNode(join);
        }

        this.nodesItems = null;
        transitionFitter.clear();
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
        if (newName != null) {
            this.newNodeName = newName.trim();
        } else {
            this.newNodeName = null;
        }
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

    public List<Node> getNodes() {
        if (nodes == null) {
            nodes = getProcessBuilder().getInstance().getNodes();
        }
        return nodes;
    }
    
    public Node getNodeByName(String nodeName) {
    	for (Node node : nodes) {
            if (node.toString().equals(nodeName)) {
                return node;
            }
        }
    	return null;
    }

    public List<Node> getNodes(String type) {
        List<Node> nodeList = new ArrayList<Node>(nodes);
        for (Iterator<Node> iterator = nodeList.iterator(); iterator.hasNext();) {
            Node n = iterator.next();
            if ("from".equals(type) && (n instanceof EndState)) {
                iterator.remove();
            }
            if ("to".equals(type) && (n instanceof StartState)) {
                iterator.remove();
            }
        }
        return nodeList;
    }

    public List<SelectItem> getNodesItems() {
        if (nodesItems == null) {
            List<Node> list = getProcessBuilder().getInstance().getNodes();
            if (list != null) {
                nodesItems = new ArrayList<SelectItem>();
                for (Node node : list) {
                    nodesItems.add(new SelectItem(node.toString(), node.getName()));
                }
                Collections.sort(nodesItems, bySelectItemLabelAsc());
                nodesItems.add(0,new SelectItem(null, infoxMessages.get("process.task.select")));
            }
        }
        return nodesItems;
    }

    public void setNodesItems(List<SelectItem> nodesList) {
        this.nodesItems = nodesList;
    }

    public List<SelectItem> getNodesTransitionItems(String type) {
        List<SelectItem> nodeItemsList = new ArrayList<SelectItem>();
        for (Node node : getNodes(type)) {
            nodeItemsList.add(new SelectItem(node, node.getName()));
        }
        Collections.sort(nodeItemsList, bySelectItemLabelAsc());
        nodeItemsList.add(0, new SelectItem(null, "Selecione..."));
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
                Number idNodeModificado = jbpmNodeManager.findNodeIdByIdProcessDefinitionAndName(getProcessBuilder().getIdProcessDefinition(), nodeName);
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
        this.currentNode = cNode;
        tf.getTasks();
        tf.clear();
        Map<Node, List<TaskHandler>> taskNodeMap = getProcessBuilder().getTaskNodeMap();
        if (taskNodeMap != null && taskNodeMap.containsKey(cNode)) {
            List<TaskHandler> list = taskNodeMap.get(cNode);
            if (!list.isEmpty()) {
                tf.setCurrentTask(list.get(0));
            }
        }
        nodeHandler = new NodeHandler(cNode);
        newNodeType = NodeTypeConstants.TASK;
        getProcessBuilder().getTransitionFitter().clearArrivingAndLeavingTransitions();
        getProcessBuilder().getTaskFitter().setTypeList(null);
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
                } else if (currentNode instanceof InfoxMailNode) {
                    type = "mail";
                }
            }
        }
        return type;
    }

    public List<String[]> getNodeTypes() {
        List<String[]> list = new ArrayList<String[]>();
        list.add(new String[] { NodeTypeConstants.START_STATE, infoxMessages.get("process.node.type.start") });
        list.add(new String[] { NodeTypeConstants.TASK, infoxMessages.get("process.node.type.task") });
        list.add(new String[] { NodeTypeConstants.DECISION, infoxMessages.get("process.node.type.decision") });
        list.add(new String[] { NodeTypeConstants.MAIL_NODE, infoxMessages.get("process.node.type.mail") });
        list.add(new String[] { NodeTypeConstants.FORK, infoxMessages.get("process.node.type.fork") });
        list.add(new String[] { NodeTypeConstants.JOIN, infoxMessages.get("process.node.type.join") });
        list.add(new String[] { NodeTypeConstants.PROCESS_STATE, infoxMessages.get("process.node.type.subprocess") });
        list.add(new String[] { NodeTypeConstants.NODE, infoxMessages.get("process.node.type.system") });
        list.add(new String[] { NodeTypeConstants.END_STATE, infoxMessages.get("process.node.type.end") });
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
    
    public String getNodeType() {
        if (currentNode instanceof TaskNode) {
            return NodeTypeConstants.TASK;
        }
        if (currentNode instanceof InfoxMailNode) {
            return NodeTypeConstants.MAIL_NODE;
        }
        if (currentNode instanceof DecisionNode) {
            return NodeTypeConstants.DECISION;
        }
        if (currentNode instanceof StartState) {
            return NodeTypeConstants.START_STATE;
        }
        if (currentNode instanceof EndState) {
            return NodeTypeConstants.END_STATE;
        }
        if (currentNode instanceof ProcessState) {
            return NodeTypeConstants.PROCESS_STATE;
        }
        if (currentNode instanceof Fork) {
            return NodeTypeConstants.FORK;
        }
        if (currentNode instanceof Join) {
            return NodeTypeConstants.JOIN;
        }
        return NodeTypeConstants.NODE;
    }

    public String getIcon(Node node) {
        String icon = node.getNodeType().toString();
        if (node instanceof InfoxMailNode) {
            icon = NodeTypeConstants.MAIL_NODE;
        }
        if (node instanceof ProcessState) {
            icon = NodeTypeConstants.PROCESS_STATE;
        }
        return icon;
    }

    public void setCurrentNode(TransitionHandler t, String type) {
        if ("from".equals(type)) {
            setCurrentNode(t.getTransition().getFrom());
        } else {
            setCurrentNode(t.getTransition().getTo());
        }
    }

    public Map<Number, String> getModifiedNodes() {
        return modifiedNodes;
    }

    public void setModifiedNodes(Map<Number, String> modifiedNodes) {
        this.modifiedNodes = modifiedNodes;
    }

    public void modifyNodes() {
        jbpmNodeManager.atualizarNodesModificados(modifiedNodes);
        modifiedNodes = new HashMap<Number, String>();
    }

    @Override
    public void clear() {
        currentNode = null;
        nodes = null;
        nodesItems = null;
    }

    public void setCurrentNodeName(String name) {
        if (name != null) {
            getCurrentNode().setName(name.trim());
        } else {
            getCurrentNode().setName(null);
        }
    }
    
    public boolean canRemove(Node node) {
        String nodeType = node.getNodeType().toString();
        if (nodeType.equals(NodeTypeConstants.START_STATE) || nodeType.equals(NodeTypeConstants.JOIN)) {
            return false;
        }
        return !getProcessBuilder().existemProcessosAssociadosAoFluxo();
    }
    
    public String getCurrentDecisionExpression() {
        return ((DecisionNode) getCurrentNode()).getDecisionExpression();
    }
    
    public void setCurrentDecisionExpression(String expression) {
        DecisionNode decision = (DecisionNode) getCurrentNode();
        if (expression != null) {
            try {
                ELParser.parse(expression);
            } catch (ELException e) {
                LOG.warn("Erro de sintaxe na expressão do nó de decisão " + decision.getName(), e);
                FacesMessages.instance().clear();
                FacesMessages.instance().add("Erro de sintaxe na expressão");
                return;
            }
        }
        decision.setDecisionExpression(expression);
    }
    
    public List<StatusProcesso> getStatusProcessoList() {
        return statusProcessoManager.findAll();
    }
    
    public List<ClassificacaoDocumento> getClassificacoesDocumento() {
    	if (classificacoesDocumento == null) {
    		classificacoesDocumento = classificacaoDocumentoFacade.getUseableClassificacaoDocumento(true);
    	}
		return classificacoesDocumento;
	}
    
    public boolean canAddCatchSignalToNode() {
        if (currentNode == null) return false;
        return NodeTypeConstants.START_STATE.equals(getNodeType())
                || NodeTypeConstants.TASK.equals(getNodeType())
                || NodeTypeConstants.PROCESS_STATE.equals(getNodeType());
    }
    
    public boolean canAddDispatcherSignalToNode() {
        if (currentNode == null) return false;
        return NodeTypeConstants.NODE.equals(getNodeType());
    }
    
    public List<Signal> getSignals() {
        return signals;
    }
    
    public List<Signal> getSinaisDisponiveis() {
        List<Signal> sinaisDisponiveis = new ArrayList<>();
        if (currentNode == null) return sinaisDisponiveis;
        for (Signal signal : signals) {
            String eventType = Event.getListenerEventType(currentNode, signal.getCodigo());
            if (currentNode.getEvents() == null || (signal.getAtivo()
                    && !currentNode.getEvents().containsKey(eventType))) {
                sinaisDisponiveis.add(signal);
            }
        }
        return sinaisDisponiveis;
    }
    
    public String getSignalLabel(Event event) {
        for (Signal signal : signals) {
            if (event.getEventType().endsWith(signal.getCodigo())){
                return signal.getNome();
            }
        }
        return "-";
    }
    
    public String getSignalTransition(Event event) {
        SignalConfigurationBean signalConfigurationBean = SignalConfigurationBean.fromJson(event.getConfiguration());
        return currentNode.getLeavingTransition(signalConfigurationBean.getTransitionKey()).getName();
    }
    
    public String getSignalCondition(Event event) {
        SignalConfigurationBean signalConfigurationBean = SignalConfigurationBean.fromJson(event.getConfiguration());
        return signalConfigurationBean.getCondition();
    }
    
    public Collection<Event> getCatchSignalEvents() {
        List<Event> listeners = new ArrayList<>();
        if (currentNode != null && currentNode.getEvents() != null) {
            for (Event event : currentNode.getEvents().values()) {
                if (event.isListener()) {
                    listeners.add(event);
                }
            }
        }
        return listeners;
    }
    
    public Collection<Event> getDispatchSignalEvents() {
        List<Event> listeners = new ArrayList<>();
        if (currentNode != null && currentNode.getEvents() != null) {
            for (Event event : currentNode.getEvents().values()) {
                if (event.isListener()) {
                    listeners.add(event);
                }
            }
        }
        return listeners;
    }
    
    public Event getDispatcherSignal() {
        if (currentNode != null && currentNode.getEvents() != null) {
            for (Event event : currentNode.getEvents().values()) {
                if (Event.EVENTTYPE_DISPATCHER.equals(event.getEventType())) {
                    return event;
                }
            }
        }
        return null;
    }
    
    public void addDispatcherSignal(ActionEvent actionEvent) {
        
    }
    
    public void addCatchSignal(ActionEvent actionEvent) {
        Map<String, String> request = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String inputNome = (String) actionEvent.getComponent().getAttributes().get("listenerValue");
        String inputTransicao = (String) actionEvent.getComponent().getAttributes().get("transitionValue");
        String inputCondition = (String) actionEvent.getComponent().getAttributes().get("conditionValue");
        String codigo = request.get(inputNome);
        String transitionKey = request.get(inputTransicao);
        String condition = request.get(inputCondition);
        Event event = new Event(Event.getListenerEventType(currentNode, codigo));
        SignalConfigurationBean signalConfigurationBean = new SignalConfigurationBean(transitionKey, condition);
        event.setConfiguration(signalConfigurationBean.toJson());
        currentNode.addEvent(event);
        JsfUtil.clear(inputNome, inputTransicao, inputCondition);
    }
    
    public void removeCatchSignal(Event event) {
        currentNode.removeEvent(event);
    }
    
}
