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

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.Decision;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.Fork;
import org.jbpm.graph.node.Join;
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
import br.com.infox.ibpm.node.InfoxMailNode;
import br.com.infox.ibpm.node.constants.NodeTypeConstants;
import br.com.infox.ibpm.node.handler.NodeHandler;
import br.com.infox.ibpm.node.manager.JbpmNodeManager;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.sinal.DispatcherConfiguration;
import br.com.infox.ibpm.sinal.Signal;
import br.com.infox.ibpm.sinal.SignalConfigurationBean;
import br.com.infox.ibpm.sinal.SignalDao;
import br.com.infox.ibpm.sinal.SignalParam;
import br.com.infox.ibpm.sinal.SignalParam.Type;
import br.com.infox.ibpm.transition.TransitionHandler;
import br.com.infox.jsf.util.JsfUtil;
import br.com.infox.seam.util.ComponentUtil;

@Named
@ViewScoped
public class NodeFitter extends Fitter implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Node> nodes;
    private List<SelectItem> nodesItems;
    private Map<Node, String> nodeMessageMap = new HashMap<Node, String>();
    private Node currentNode;
    private NodeHandler nodeHandler;
    private String nodeName;
    private Map<Number, String> modifiedNodes = new HashMap<Number, String>();
    private List<ClassificacaoDocumento> classificacoesDocumento;
    private List<Signal> signals;
    
    //Controlle dos Observadores de sinal
    private String codigoCatchSignal;
    private String transicaoCatchSignal;
    private String condicaoCatchSignal;
    private boolean managedCatchSignal = false;

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
    
    // #72877
    public void addNewNode() {
        /*Class<? extends Node> nodeType = NodeTypes.getNodeType(getNodeType(newNodeType));
        if (nodeType == null) return;
        
        ProcessDefinition processDefinition = getProcessBuilder().getInstance();
        Node node = null;
        try {
            node = nodeType.newInstance();
        } catch (Exception e) {
            LOG.error("addNewNode()", e);
            return;
        }
        if (nodeType.equals(InfoxMailNode.class) || nodeType.equals(Node.class)){
            node.setAsync(true);
        }
        node.setName(newNodeName);
        node.setKey(BpmUtil.generateKey());
        processDefinition.addNode(node);
        nodes = processDefinition.getNodes();
        // Se foi informado newNodeAfter, procura para inserir
        if (newNodeAfter != null) {
            int i = nodes.indexOf(newNodeAfter);
            processDefinition.reorderNode(nodes.indexOf(node), i + 1);
        } else {
            // Senão coloca antes do primeiro EndState
            int i = nodes.size() - 1;
            do {
                i--;
            } while (nodes.get(i) instanceof EndState);
            processDefinition.reorderNode(nodes.indexOf(node), i + 1);
        }
        // insere o novo nó entre os nós da transição selecionada
        // Se for EndState, liga apenas ao newNodeAfter
        TransitionHandler newNodeTransition = getProcessBuilder().getTransitionFitter().getNewNodeTransition();
        if (nodeType.equals(EndState.class)) {
            Transition transition = new Transition();
            transition.setFrom(newNodeAfter);
            node.addArrivingTransition(transition);
            transition.setName(node.getName());
            transition.setKey(BpmUtil.generateKey());
            newNodeAfter.addLeavingTransition(transition);
        } else if (newNodeTransition != null && newNodeTransition.getTransition() != null) {
            Transition transition = new Transition();
            Transition oldT = newNodeTransition.getTransition();
            transition.setDescription(oldT.getDescription());
            Node to = newNodeTransition.getTransition().getTo();
            transition.setName(to.getName());
            transition.setKey(BpmUtil.generateKey());
            transition.setProcessDefinition(oldT.getProcessDefinition());

            /*
             * Não reordenar as linhas de código marcadas pelo bloco abaixo
             * (ver tarefa #35099) A alteração dos atributos from e name da
             * transição altera seu hashcode causando erros
             */
            // INÍCIO BLOCO //
            /*node.addLeavingTransition(transition);
            to.removeArrivingTransition(oldT);
            newNodeTransition.setName(node.getName());
            node.addArrivingTransition(oldT);
            to.addArrivingTransition(transition);
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
//            getProcessBuilder().getTaskFitter().addTask();
        } else if (nodeType.equals(StartState.class)) {
            getProcessBuilder().getTaskFitter().addStartStateTask();
        }
        getProcessBuilder().getTransitionFitter().clearNewNodeTransition();
        getProcessBuilder().getTransitionFitter().clear();
        getProcessBuilder().getTransitionFitter().checkTransitions();*/
    }

    public List<ModeloDocumento> getModeloDocumentoList() {
        return ((ModeloDocumentoManager) ComponentUtil.getComponent(ModeloDocumentoManager.NAME)).getModeloDocumentoList();
    }

    // #72877
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

    // #72877
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

    // #72877
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
//            removeTransitions(transitions);
        }
        Collection<Transition> transitionSet = node.getArrivingTransitions();
        if (transitionSet != null) {
//            removeTransitions(transitionSet);
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

    public void setNodeIndex(String key) {
    	for (Node node : getNodes()) {
    		if (node.getKey().equals(key)) {
    			setCurrentNode(node);
    			break;
    		}
    	}
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
        TaskFitter taskFitter = getProcessBuilder().getTaskFitter();
        this.currentNode = cNode;
        taskFitter.getTasks();
        nodeHandler = new NodeHandler(cNode);
        getProcessBuilder().getTransitionFitter().clearArrivingAndLeavingTransitions();
        getProcessBuilder().getTaskFitter().setTypeList(null);
        if (taskFitter.getCurrentTask() != null) {
            taskFitter.getCurrentTask().clearHasTaskPage();
        }
    }

    public String getNodeForm() {
        String type = "empty";
        if (currentNode != null) {
            if (NodeType.Node.equals(currentNode.getNodeType())) {
                if (currentNode instanceof ProcessState) {
                    type = "processState";
                } else if (currentNode instanceof InfoxMailNode) {
                    type = "mail";
                }
            }
        }
        return type;
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
        if (currentNode instanceof Decision) {
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
    
    public Signal getSignal(String codigo) {
        for (Signal signal : signals) {
            if (signal.getCodigo().equals(codigo)) {
                return signal;
            }
        }
        return null;
    }
    
    public List<Signal> getSinaisDisponiveis() {
        List<Signal> sinaisDisponiveis = new ArrayList<>();
        if (currentNode == null) return sinaisDisponiveis;
        for (Signal signal : signals) {
            String eventType = Event.getListenerEventType(signal.getCodigo());
            if (currentNode.getEvents() == null || (signal.getAtivo()
                    && (!currentNode.getEvents().containsKey(eventType)) || signal.getCodigo().equals(getCodigoCatchSignal()))) {
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
    
    public List<SignalParam> getDispatchParams() {
        Event event = getDispatcherSignal();
        DispatcherConfiguration dispatcherConfiguration = DispatcherConfiguration.fromJson(event.getConfiguration());
        return dispatcherConfiguration.getSignalParams();
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
    
    public String getDispatcherSignalName() {
        DispatcherConfiguration dispatcherConfiguration = DispatcherConfiguration.fromJson(getDispatcherSignal().getConfiguration());
        return getSignal(dispatcherConfiguration.getCodigoSinal()).getNome();
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
    
    public Type[] getParamTypes() {
        return Type.values();
    }
    
    public void addCatchSignal() {
        Event event = new Event(Event.getListenerEventType(codigoCatchSignal));
        SignalConfigurationBean signalConfigurationBean = new SignalConfigurationBean(transicaoCatchSignal, condicaoCatchSignal);
        event.setConfiguration(signalConfigurationBean.toJson());
        currentNode.addEvent(event);
        clearCacheCatchSignal();
    }

	private void clearCacheCatchSignal() {
		codigoCatchSignal = null;
        transicaoCatchSignal = null;
        condicaoCatchSignal = null;
        managedCatchSignal = false;
	}
    
    public void addDispatcherSignal(ActionEvent actionEvent) {
        Map<String, String> request = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String inputCodigo = (String) actionEvent.getComponent().getAttributes().get("dispatcherCodigo");
        String codigo = request.get(inputCodigo);
        Event event = new Event(Event.EVENTTYPE_DISPATCHER);
        DispatcherConfiguration signalConfigurationBean = new DispatcherConfiguration(codigo);
        event.setConfiguration(signalConfigurationBean.toJson());
        Action actionRef = new Action();
        actionRef.setName("dispatcher-" + codigo + "_" + currentNode.getKey());
        actionRef.setActionExpression(String.format("#{bpmExpressionService.dispatchSignal('%s')}", codigo));
        event.addAction(actionRef);
        currentNode.addEvent(event);
        Action action = new Action();
        action.setReferencedAction(actionRef);
        currentNode.setAction(action);
        JsfUtil.clear(inputCodigo);
    }
    
    public void addDispatcherParamSignal(ActionEvent actionEvent) {
        Map<String, String> request = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String inputNome = (String) actionEvent.getComponent().getAttributes().get("dispatcherParamName");
        String inputValue = (String) actionEvent.getComponent().getAttributes().get("dispatcherParamValue");
        String inputType = (String) actionEvent.getComponent().getAttributes().get("dispatcherParamType");
        String nome = request.get(inputNome);
        String value = request.get(inputValue);
        Type type = Type.valueOf(request.get(inputType));
        Event event = getDispatcherSignal();
        DispatcherConfiguration dispatcherConfiguration = DispatcherConfiguration.fromJson(event.getConfiguration());
        SignalParam newSignalParam = new SignalParam(nome, value, type);
        if (dispatcherConfiguration.getSignalParams() != null && dispatcherConfiguration.getSignalParams().contains(newSignalParam)) {
            FacesMessages.instance().add("Já existe parâmetro com esse nome!");
            FacesContext.getCurrentInstance().validationFailed();
        } else {
            dispatcherConfiguration.addSignalParam(newSignalParam);
            event.setConfiguration(dispatcherConfiguration.toJson());
        }
        JsfUtil.clear(inputNome, inputValue, inputType);
    }
    
    public void removeCatchSignal(Event event) {
        if (getCodigoSignalByEvent(event).equals(codigoCatchSignal)){
        	clearCacheCatchSignal();
        }
        currentNode.removeEvent(event);
    }
    
    public void selectCatchSignal(Event event) {
    	setCodigoCatchSignal(getCodigoSignalByEvent(event));
    	SignalConfigurationBean signalConfigurationBean = SignalConfigurationBean.fromJson(event.getConfiguration());
    	setTransicaoCatchSignal(signalConfigurationBean.getTransitionKey());
    	setCondicaoCatchSignal(signalConfigurationBean.getCondition());
    	setManagedCatchSignal(true);
    }

	private String getCodigoSignalByEvent(Event event) {
		return event.getEventType().substring(Event.EVENTTYPE_LISTENER.length() + 1);
	}
    
    public void saveCatchSignal() {
    	Event event = currentNode.getEvent(Event.getListenerEventType(getCodigoCatchSignal())); 
    	SignalConfigurationBean signalConfigurationBean = new SignalConfigurationBean(transicaoCatchSignal, condicaoCatchSignal);
        event.setConfiguration(signalConfigurationBean.toJson());
    	clearCacheCatchSignal();
    }
    
    public void removeDispatchSignal() {
        currentNode.removeEvent(getDispatcherSignal());
        currentNode.setAction(null);
    }
    
    public void removeDispatcherParam(SignalParam signalParam) {
        Event event = getDispatcherSignal();
        DispatcherConfiguration dispatcherConfiguration = DispatcherConfiguration.fromJson(event.getConfiguration());
        dispatcherConfiguration.getSignalParams().remove(signalParam);
        event.setConfiguration(dispatcherConfiguration.toJson());
    }

	public String getCodigoCatchSignal() {
		return codigoCatchSignal;
	}

	public void setCodigoCatchSignal(String codigoCatchSignal) {
		this.codigoCatchSignal = codigoCatchSignal;
	}

	public String getTransicaoCatchSignal() {
		return transicaoCatchSignal;
	}

	public void setTransicaoCatchSignal(String transicaoCatchSignal) {
		this.transicaoCatchSignal = transicaoCatchSignal;
	}

	public String getCondicaoCatchSignal() {
		return condicaoCatchSignal;
	}

	public void setCondicaoCatchSignal(String condicaoCatchSignal) {
		this.condicaoCatchSignal = condicaoCatchSignal;
	}

	public boolean isManagedCatchSignal() {
		return this.managedCatchSignal;
	}
	
	public void setManagedCatchSignal(boolean managedCatchSignal) {
		this.managedCatchSignal = managedCatchSignal;
	}
    
}
