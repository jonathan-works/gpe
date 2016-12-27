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
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.xml.sax.InputSource;

import com.google.common.base.Strings;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.epp.cdi.transaction.Transactional.TxType;
import br.com.infox.epp.fluxo.entity.DefinicaoProcesso;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.RaiaPerfilManager;
import br.com.infox.epp.fluxo.merger.model.MergePoint;
import br.com.infox.epp.fluxo.merger.model.MergePointsBundle;
import br.com.infox.epp.fluxo.service.HistoricoProcessDefinitionService;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.tarefa.manager.TarefaJbpmManager;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.ibpm.jpdl.InfoxJpdlXmlReader;
import br.com.infox.ibpm.node.InfoxMailNode;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.swimlane.SwimlaneInstanceSearch;
import br.com.infox.ibpm.task.dao.TaskInstanceDAO;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class FluxoMergeService {

    @Inject
    private EntityManager entityManager;
    @Inject
    private FluxoManager fluxoManager;
    @Inject
    private RaiaPerfilManager raiaPerfilManager;
    @Inject
    private SwimlaneInstanceSearch swimlaneInstanceSearch;
    @Inject
    private TaskInstanceDAO taskInstanceDAO;
    @Inject
    private TarefaManager tarefaManager;
    @Inject
    private TarefaJbpmManager tarefaJbpmManager;
    @Inject
    private ProcessoManager processoManager;
    @Inject
    private HistoricoProcessDefinitionService historicoProcessDefinitionService;
    @Inject
    @GenericDao
    private Dao<DefinicaoProcesso, Long> definicaoProcessoDao;

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
        String modifiedXml = fluxo.getDefinicaoProcesso().getXml();
        String publishedXml = fluxo.getDefinicaoProcesso().getXmlExecucao();
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
        String modifiedXml = fluxo.getDefinicaoProcesso().getXml();
        String publishedXml = fluxo.getDefinicaoProcesso().getXmlExecucao();
        ProcessDefinition modifiedProcessDef = jpdlToProcessDefinition(modifiedXml);
        
        validateJbpmGraph(modifiedProcessDef);
        validateMailNode(modifiedProcessDef);
        validateSubProcessNode(modifiedProcessDef);
        validateVariables(modifiedProcessDef);
        
        if (publishedXml == null || publishedXml.isEmpty()) {
            modifiedProcessDef.setName(fluxo.getFluxo());
            doDeploy(fluxo, modifiedProcessDef);
        } else if (!Objects.equals(modifiedXml, publishedXml)) {
            ProcessDefinition publishedProcessDef = jpdlToProcessDefinition(publishedXml);
            mergePointsBundle = verifyMerge(publishedProcessDef, modifiedProcessDef);
            if (mergePointsBundle.isValid()) {
                modifiedProcessDef.setName(fluxo.getFluxo());
                doDeploy(fluxo, modifiedProcessDef);
            } else {
            	fluxo.setPublicado(false);
            	throw new FluxoMergeException(mergePointsBundle);
            }
        }
        return mergePointsBundle;
    }
    
    private void doDeploy(Fluxo fluxo, ProcessDefinition newProcessDefinition) {
	    try {
	    	raiaPerfilManager.atualizarRaias(fluxo, newProcessDefinition.getTaskMgmtDefinition().getSwimlanes());
	        
            JbpmUtil.getGraphSession().deployProcessDefinition(newProcessDefinition);
            JbpmUtil.getJbpmSession().flush();
            fluxo.getDefinicaoProcesso().setXmlExecucao(fluxo.getDefinicaoProcesso().getXml());
            fluxo.getDefinicaoProcesso().setSvgExecucao(fluxo.getDefinicaoProcesso().getSvg());
            
            if (!fluxo.getPublicado()) {
                fluxo.setPublicado(true);
            }
            
            fluxo.setDefinicaoProcesso(definicaoProcessoDao.update(fluxo.getDefinicaoProcesso()));
            fluxoManager.update(fluxo);
            updatePostDeploy(newProcessDefinition);
            
            atualizarRaiaPooledActors(newProcessDefinition.getId());
            
            JbpmUtil.instance().deleteTimers(newProcessDefinition);
            JbpmUtil.instance().createTimers(newProcessDefinition);
            
            historicoProcessDefinitionService.limparHistoricos(fluxo.getDefinicaoProcesso());
        } catch (Exception e) {
            fluxo.setPublicado(false);
            throw new BusinessRollbackException(e);
        }
    }
    
    private void atualizarRaiaPooledActors(Long idProcessDefinition) {
       EntityManager entityManager = EntityManagerProducer.instance().getEntityManagerNotManaged();
       try {
           List<SwimlaneInstance> swimlaneInstances = swimlaneInstanceSearch.getSwimlaneInstancesByProcessDefinition(idProcessDefinition, entityManager);
           for (SwimlaneInstance swimlaneInstance : swimlaneInstances) {
               String[] pooledActorIds = swimlaneInstance.getSwimlane().getPooledActorsExpression().split(",");
               swimlaneInstance.setPooledActors(pooledActorIds);
           }
           entityManager.flush();
           List<TaskInstance> taskInstances = taskInstanceDAO.getTaskInstancesOpen(idProcessDefinition, entityManager);
           for (TaskInstance taskInstance : taskInstances) {
               ExecutionContext executionContext = new ExecutionContext(taskInstance.getToken());
               taskInstance.assign(executionContext);
               if (taskInstance.getSwimlaneInstance() != null && taskInstance.getSwimlaneInstance().getId() == 0) {
            	   entityManager.persist(taskInstance.getSwimlaneInstance());
               }
           }
           entityManager.flush();
       } finally {
           if (entityManager.isOpen()) {
               entityManager.close();
           }
       }
	}

    private void updatePostDeploy(ProcessDefinition processDefinition) {
        processoManager.atualizarProcessos(processDefinition.getId(), processDefinition.getName());
        tarefaManager.encontrarNovasTarefas();
        tarefaJbpmManager.inserirVersoesTarefas();
    }

    private void validateVariables(ProcessDefinition processDefinition) {
        List<Node> nodes = processDefinition.getNodes();
        for (Node node : nodes) {
            if (node.getNodeType().equals(NodeType.Task)) {
                validateVariablesTaskNode(node.getName(), ((TaskNode) node).getTask(node.getName()).getTaskController());
            } else if (node.getNodeType().equals(NodeType.StartState)) {
                Task startTask = ((StartState) node).getProcessDefinition().getTaskMgmtDefinition().getStartTask();
                if (startTask != null) {
                    TaskController taskController = startTask.getTaskController();
                    validateVariablesNoInicio(taskController);
                }
            }
        }
    }

    private void validateVariablesTaskNode(String nodeName, TaskController taskController) {
        if (taskController != null) {
            List<VariableAccess> variables = taskController.getVariableAccesses();
            for (VariableAccess variable : variables) {
                String[] tokens = variable.getMappedName().split(":");
                if (tokens.length == 1) {
                    throw new BusinessRollbackException(MessageFormat.format(
                            InfoxMessages.getInstance().get("processBuilder.validationError.task.varSemNome"), nodeName));
                } else if (VariableType.NULL.name().equals(tokens[0])) {
                    throw new BusinessRollbackException(MessageFormat.format(
                            InfoxMessages.getInstance().get("processBuilder.validationError.task.varSemTipo"), tokens[1], nodeName));
                } else if (VariableType.DATE.name().equals(tokens[0]) && variable.getConfiguration() == null) {
                    throw new BusinessRollbackException(MessageFormat.format(
                            InfoxMessages.getInstance().get("processBuilder.validationError.task.dataSemValidacao"), tokens[1], nodeName));
                } else if (VariableType.ENUMERATION.name().equals(tokens[0]) && variable.getConfiguration() == null) {
                    throw new BusinessRollbackException(MessageFormat.format(
                            InfoxMessages.getInstance().get("processBuilder.validationError.task.listaSemValor"), tokens[1], nodeName));
                } else if (VariableType.ENUMERATION_MULTIPLE.name().equals(tokens[0]) && variable.getConfiguration() == null) {
                    throw new BusinessRollbackException(MessageFormat.format(
                            InfoxMessages.getInstance().get("processBuilder.validationError.task.lMutilpaSemValor"), tokens[1], nodeName));
                } else if (VariableType.FRAGMENT.name().equals(tokens[0]) && tokens.length < 3) {
                    throw new BusinessRollbackException(MessageFormat.format(
                            InfoxMessages.getInstance().get("processBuilder.validationError.task.listaPersonSemValor"), tokens[1], nodeName));
                }
            }
        }
    }

    private void validateVariablesNoInicio(TaskController taskController) {
        if (taskController != null) {
            List<VariableAccess> variables = taskController.getVariableAccesses();
            for (VariableAccess variable : variables) {
                String[] tokens = variable.getMappedName().split(":");
                if (tokens.length == 1) {
                    throw new BusinessRollbackException(InfoxMessages.getInstance().get("processBuilder.validationError.start.varSemNome"));
                } else if (VariableType.NULL.name().equals(tokens[0])) {
                    throw new BusinessRollbackException(MessageFormat.format(
                            InfoxMessages.getInstance().get("processBuilder.validationError.start.varSemTipo"), tokens[1]));
                } else if (VariableType.DATE.name().equals(tokens[0]) && variable.getConfiguration() == null) {
                    throw new BusinessRollbackException(MessageFormat.format(
                            InfoxMessages.getInstance().get("processBuilder.validationError.start.dataSemValidacao"), tokens[1]));
                } else if (VariableType.ENUMERATION.name().equals(tokens[0]) && variable.getConfiguration() == null) {
                    throw new BusinessRollbackException(MessageFormat.format(
                            InfoxMessages.getInstance().get("processBuilder.validationError.start.listaSemValor"), tokens[1]));
                } else if (VariableType.ENUMERATION_MULTIPLE.name().equals(tokens[0]) && variable.getConfiguration() == null) {
                    throw new BusinessRollbackException(MessageFormat.format(
                            InfoxMessages.getInstance().get("processBuilder.validationError.start.lMutilpaSemValor"), tokens[1]));
                } else if (VariableType.FRAGMENT.name().equals(tokens[0]) && tokens.length < 3) {
                    throw new BusinessRollbackException(MessageFormat.format(
                            InfoxMessages.getInstance().get("processBuilder.validationError.start.listaPersonSemValor"), tokens[1]));
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
                    throw new BusinessRollbackException(MessageFormat.format(
                            InfoxMessages.getInstance().get("processBuilder.validationError.subProcesso"), subprocess.getName()));
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
                    throw new BusinessRollbackException(MessageFormat.format(
                            InfoxMessages.getInstance().get("processBuilder.validationError.emailDestinatario"), mailNode.getName()));
                }
                if (mailNode.getModeloDocumento() == null) {
                    throw new BusinessRollbackException(MessageFormat.format(
                            InfoxMessages.getInstance().get("processBuilder.validationError.emailModeloDocumento"), mailNode.getName()));
                }
            }
        }
    }
    
    private void validateJbpmGraph(ProcessDefinition processDefinition) {
        List<Node> nodes = processDefinition.getNodes();
        for (Node node : nodes) {
            if (!node.getNodeType().equals(NodeType.EndState)
                    && (node.getLeavingTransitions() == null || node.getLeavingTransitions().isEmpty())) {
                throw new BusinessRollbackException(MessageFormat.format(
                        InfoxMessages.getInstance().get("processBuilder.validationError.transicao"), node.getName()));
            }
        }

        Node start = processDefinition.getStartState();
        Set<Node> visitedNodes = new HashSet<>();
        if (!findPathToEndState(start, visitedNodes, false)) {
            throw new BusinessRollbackException(InfoxMessages.getInstance().get("processBuilder.validationError.saida"));
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
