package br.com.infox.ibpm.process.definition.fitter;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.el.ELException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.jboss.el.parser.ELParser;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
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
import br.com.infox.ibpm.node.converter.NodeConverter;
import br.com.infox.ibpm.node.handler.NodeHandler;
import br.com.infox.ibpm.node.manager.JbpmNodeManager;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.task.handler.TaskHandler;
import br.com.infox.ibpm.transition.TransitionHandler;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Name(NodeFitter.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
@Stateful
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
    private Map<Number, String> modifiedNodes = new HashMap<Number, String>();
    private List<ClassificacaoDocumento> classificacoesDocumento;

    @In
    private JbpmNodeManager jbpmNodeManager;
    @In
    private TransitionFitter transitionFitter;
    @In
    private StatusProcessoManager statusProcessoManager;
    @In
    private InfoxMessages infoxMessages;
    @In
    private ClassificacaoDocumentoFacade classificacaoDocumentoFacade;
    @Inject
    private FluxoMergeService fluxoMergeService;

    /**
     * Método foi necessário ser adicionado devido ao Seam ter problemas
     * com anotação @Stateful
     */
    @Remove
    public void destroy(){
    }
    
    @SuppressWarnings(UNCHECKED)
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
            if (node.getNodeType().equals(org.jbpm.graph.def.Node.NodeType.Node)){
                node.setAsync(true);
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

    @SuppressWarnings(UNCHECKED)
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
        while (transitions.size() > 0) {
            Transition t = transitions.iterator().next();
            removeTransition(t);
        }
    }

    @SuppressWarnings(UNCHECKED)
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

    @SuppressWarnings(UNCHECKED)
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
            if ("from".equals(type) && (n instanceof EndState)) {
                iterator.remove();
            }
            if ("to".equals(type) && (n instanceof StartState)) {
                iterator.remove();
            }
        }
        return nodeList;
    }

    @SuppressWarnings(UNCHECKED)
    public List<SelectItem> getNodesItems() {
        if (nodesItems == null) {
            List<Node> list = getProcessBuilder().getInstance().getNodes();
            if (list != null) {
                nodesItems = new ArrayList<SelectItem>();
                nodesItems.add(new SelectItem(null, infoxMessages.get("process.task.select")));
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
}
