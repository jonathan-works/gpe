package br.com.infox.epp.fluxo.merger.service;

import java.io.StringReader;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.TaskController;
import org.xml.sax.InputSource;

import com.google.common.base.Strings;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.epp.cdi.transaction.Transactional.TxType;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.merger.model.MergePoint;
import br.com.infox.epp.fluxo.merger.model.MergePointsBundle;
import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.node.InfoxMailNode;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class FluxoMergeService {

    @Inject
    private EntityManager entityManager;
    @Inject
    private ProcessBuilder processBuilder;

    private List<MergePoint> getMergePoints(ProcessDefinition processDefinition){
        TypedQuery<MergePoint> query = entityManager.createQuery(GET_MERGE_POINTS_QUERY, MergePoint.class);
        query = query.setParameter(PROCESS_INSTANCE, processDefinition.getName());
        return query.getResultList();
    }

    public MergePointsBundle verifyMerge(String xmlBase, String xmlReference){
        ProcessDefinition base = jpdlToProcessDefinition(xmlBase);
        ProcessDefinition reference = jpdlToProcessDefinition(xmlReference);
        return verifyMerge(base, reference);
    }
    
    public MergePointsBundle verifyMerge(ProcessDefinition base, ProcessDefinition reference){
        return new MergePointsBundle(getMergePoints(base), reference);
    }
    
    public MergePointsBundle verifyMerge(Fluxo fluxo){
        String modifiedXml = fluxo.getXml();
        String publishedXml = fluxo.getXmlExecucao();
        if (Objects.equals(modifiedXml, publishedXml)) {
            return null;
        }
        ProcessDefinition modifiedProcessDef = jpdlToProcessDefinition(modifiedXml);
        ProcessDefinition publishedProcessDef = jpdlToProcessDefinition(publishedXml);
        return verifyMerge(publishedProcessDef, modifiedProcessDef);
    }
    
    public ProcessDefinition jpdlToProcessDefinition(String xml) {
        StringReader stringReader = new StringReader(xml);
        InfoxJpdlXmlReader jpdlReader = new InfoxJpdlXmlReader(new InputSource(stringReader));
        return xml == null || xml.isEmpty() ? null : jpdlReader.readProcessDefinition();
    }

    public boolean hasActiveNode(ProcessDefinition processDefinition, Node node){
        TypedQuery<Node> query = entityManager.createQuery(GET_ACTIVE_NODE, Node.class);
        query = query.setParameter(PROCESS_INSTANCE, processDefinition.getName());
        query = query.setParameter(NODE_NAME, node.getName());
        List<Node> nodes = query.getResultList();
        return nodes != null && nodes.size() > 0;
    }
    
    @Transactional(value = TxType.REQUIRED, timeout = 1800)
    public MergePointsBundle publish(Fluxo fluxo, MergePointsBundle mergePointsBundle) {
        String modifiedXml = fluxo.getXml();
        String publishedXml = fluxo.getXmlExecucao();
        ProcessDefinition modifiedProcessDef = jpdlToProcessDefinition(modifiedXml);
        
        validateJbpmGraph(modifiedProcessDef);
        validateMailNode(modifiedProcessDef);
        validateSubProcessNode(modifiedProcessDef);
        validateVariables(modifiedProcessDef);
        
        if (publishedXml == null || publishedXml.isEmpty()) {
            modifiedProcessDef.setName(fluxo.getFluxo());
            processBuilder.load(fluxo);
            processBuilder.setInstance(modifiedProcessDef);
            boolean isDeployed = processBuilder.deploy();
            if(!isDeployed){
                throw new RuntimeException("Erro ao executar deploy!");
            }
        } else if (!Objects.equals(modifiedXml, publishedXml)) {
            ProcessDefinition publishedProcessDef = jpdlToProcessDefinition(publishedXml);
            mergePointsBundle = verifyMerge(publishedProcessDef, modifiedProcessDef);
            if (mergePointsBundle.isValid()) {
                modifiedProcessDef.setName(fluxo.getFluxo());
                processBuilder.load(fluxo);
                processBuilder.setInstance(modifiedProcessDef);
                boolean isDeployed = processBuilder.deploy();
                if(!isDeployed){
                    throw new RuntimeException("Erro ao executar deploy!");
                }
            }
            return mergePointsBundle;
        }
        return mergePointsBundle;
    }
    
    private void validateVariables(ProcessDefinition processDefinition) {
        List<Node> nodes = processDefinition.getNodes();
        for (Node node : nodes) {
            if (node.getNodeType().equals(NodeType.Task)) {
                TaskController taskController = ((TaskNode) node).getTask(node.getName()).getTaskController();
                if (taskController != null) {
                    List<VariableAccess> variables = taskController.getVariableAccesses();
                    for (VariableAccess variable : variables) {
                        String[] tokens = variable.getMappedName().split(":");
                        if (tokens.length == 1) {
                            throw new BusinessRollbackException("Existe uma variável sem nome na tarefa " + node.getName()); 
                        } else if (VariableType.NULL.name().equals(tokens[0])) {
                            throw new BusinessRollbackException("A variável " + tokens[1] + " da tarefa " + node.getName() + " não possui tipo");
                        } else if (VariableType.DATE.name().equals(tokens[0]) && tokens.length < 3) {
                            throw new BusinessRollbackException("A variável " + tokens[1] + " da tarefa " + node.getName() + " é do tipo data mas não possui tipo de validação");
                        } else if (VariableType.ENUMERATION.name().equals(tokens[0]) && tokens.length < 3) {
                            throw new BusinessRollbackException("A variável " + tokens[1] + " da tarefa " + node.getName() + " é do tipo lista de dados mas não possui lista de valores definida");
                        } else if (VariableType.ENUMERATION_MULTIPLE.name().equals(tokens[0]) && tokens.length < 3) {
                            throw new BusinessRollbackException("A variável " + tokens[1] + " da tarefa " + node.getName() + " é do tipo lista de dados (múltipla) mas não possui lista de valores definida");
                        } else if (VariableType.FRAGMENT.name().equals(tokens[0]) && tokens.length < 3) {
                            throw new BusinessRollbackException(MessageFormat.format(InfoxMessages.getInstance().get("processDefinition.variable.list.error"), tokens[1], node.getName()));
                        } 
                    }
                }
            }
        }
    }
    
    private void validateSubProcessNode(ProcessDefinition processDefinition) {
        List<Node> nodes = processDefinition.getNodes();
        for (Node node : nodes) {
            if (node instanceof ProcessState) {
                ProcessState subprocess = (ProcessState) node;
                if (Strings.isNullOrEmpty(subprocess.getSubProcessName())) {
                    throw new BusinessRollbackException("O nó " + subprocess.getName() + " é do tipo subprocesso e deve possuir um fluxo associado.");
                }
            }
        }
    }
    
    private void validateMailNode(ProcessDefinition processDefinition) {
        List<Node> nodes = processDefinition.getNodes();
        for (Node node : nodes) {
            if (node instanceof InfoxMailNode) {
                InfoxMailNode mailNode = (InfoxMailNode) node;
                if (Strings.isNullOrEmpty(mailNode.getTo())) {
                    throw new BusinessRollbackException("O nó de email " + mailNode.getName() + " deve possuir pelo menos um destinatário.");
                }
                if (mailNode.getModeloDocumento() == null) {
                    throw new BusinessRollbackException("O nó de email " + mailNode.getName() + " deve possuir um modelo de documento associado.");
                }
            }
        }
    }
    
    private void validateJbpmGraph(ProcessDefinition processDefinition) {
        List<Node> nodes = processDefinition.getNodes();
        for (Node node : nodes) {
            if (!node.getNodeType().equals(NodeType.EndState)
                    && (node.getLeavingTransitions() == null || node.getLeavingTransitions().isEmpty())) {
                throw new BusinessRollbackException("O nó " + node.getName() + " não possui transição de saída.");
            }
        }

        Node start = processDefinition.getStartState();
        Set<Node> visitedNodes = new HashSet<>();
        if (!findPathToEndState(start, visitedNodes, false)) {
            throw new BusinessRollbackException("Fluxo mal-definido, não há como alcançar o nó de término.");
        }
    }
    
    private boolean findPathToEndState(Node node, Set<Node> visitedNodes, boolean hasFoundEndState) {
        if (node.getNodeType().equals(NodeType.EndState)) {
            return true;
        }

        if (!visitedNodes.contains(node)) {
            visitedNodes.add(node);

            List<Transition> transitions = node.getLeavingTransitions();
            for (Transition t : transitions) {
                if (t.getTo() != null) {
                    hasFoundEndState = findPathToEndState(t.getTo(), visitedNodes, hasFoundEndState);
                }
            }
        }
        return hasFoundEndState;
    }
    
    private static final String PROCESS_INSTANCE = "processDefinitionName";
    private static final String NODE_NAME = "node_name";
    
    private static final String GET_MERGE_POINTS_QUERY = "select new br.com.infox.epp.fluxo.merger.model.MergePoint(node.name, count(distinct tok.id))"
            + " from org.jbpm.graph.exe.Token tok"
            + " inner join tok.processInstance procIns"
            + " inner join procIns.processDefinition pd"
            + " inner join tok.node node"
            + " where pd.name = :"+ PROCESS_INSTANCE
            + " and"
            + " tok.end IS NULL"
            + " group by node.name";
    
    private static final String GET_ACTIVE_NODE = "select node"
            + " from org.jbpm.graph.exe.Token tok"
            + " inner join tok.processInstance procIns"
            + " inner join procIns.processDefinition pd"
            + " inner join tok.node node"
            + " where pd.name = :"+ PROCESS_INSTANCE
            + " and"
            + " tok.end IS NULL"
            + " AND node.name = :" + NODE_NAME;
}
