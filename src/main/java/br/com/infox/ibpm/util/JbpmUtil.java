package br.com.infox.ibpm.util;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.scheduler.def.CreateTimerAction;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.constants.FloatFormatConstants;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.ibpm.task.manager.UsuarioTaskInstanceManager;
import br.com.infox.ibpm.variable.JbpmVariavelLabel;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Name(JbpmUtil.NAME)
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Install(dependencies = {})
public class JbpmUtil {

    private static final LogProvider LOG = Logging.getLogProvider(JbpmUtil.class);

    public static final String NAME = "jbpmUtil";
    public static final int FROM_TASK_TRANSITION = 0;
    public static final int TO_TASK_TRANSITION = 1;
    private static Map<String, String> messagesMap;

    /**
     * Busca a localização de uma tarefa
     * 
     * @param task
     * @return
     */
    public Localizacao getLocalizacao(TaskInstance task) {
        return usuarioTaskInstanceManager().getLocalizacaoTarefa(task.getId());
    }

    /**
     * Busca a localização de um processo
     * 
     * @param jbpmProcessId é o id do Processo no contexto jBPM
     *        (Processo.getIdJbpm())
     * @return retorna a primeira localização encontrada
     */
    public Localizacao getLocalizacao(long jbpmProcessId) {
        ProcessInstance pi = ManagedJbpmContext.instance().getProcessInstance(jbpmProcessId);
        Token token = pi.getRootToken();
        for (Object o : pi.getTaskMgmtInstance().getTaskInstances()) {
            TaskInstance t = (TaskInstance) o;
            if (t.getTask().getTaskNode().equals(token.getNode())) {
                return getLocalizacao(t);
            }
        }
        return null;
    }

    public static Session getJbpmSession() {
        return ManagedJbpmContext.instance().getSession();
    }
    
    public void deleteTimers(ProcessDefinition processDefinition) {
        EntityManager entityManager = EntityManagerProducer.getEntityManager();
        // Usado JPQL pois no JPA 2.0 nãp têm criteria para DELETE, somente no JPA 2.1
        String jpql = "delete from org.jbpm.job.Timer timer "
                + "where exists (select 1 from org.jbpm.graph.exe.ProcessInstance pinst "
                + "                 where timer.processInstance.id = pinst.id "
                + "                 and pinst.processDefinition.id = :processDefitinionId )";
        Query query = entityManager.createQuery(jpql).setParameter("processDefitinionId", processDefinition.getId());
        query.executeUpdate();
    }
    
    public void createTimers(ProcessDefinition processDefinition) throws Exception {
        EntityManager entityManager = EntityManagerProducer.instance().getEntityManagerTransactional();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<ProcessInstance> processInstance = cq.from(ProcessInstance.class);
        Root<TaskNode> taskNode = cq.from(TaskNode.class);
        Root<Token> token = cq.from(Token.class);
        Root<CreateTimerAction> createTimeAction = cq.from(CreateTimerAction.class);
        Join<TaskNode, Event> event = taskNode.join("events", JoinType.INNER);
        
        Subquery<Integer> subquery = cq.subquery(Integer.class);
        subquery.select(cb.literal(1));
        Root<TaskInstance> taskInstance = subquery.from(TaskInstance.class);
        Join<TaskInstance, Task> task = taskInstance.join("task", JoinType.INNER);
        subquery.where(
            cb.equal(task.<TaskNode>get("taskNode").<Long>get("id"), taskNode.<Long>get("id")),
            cb.isNull(taskInstance.get("end")),
            cb.isNotNull(taskInstance.get("create"))
        );
        
        cq.where(
            cb.equal(processInstance.<ProcessDefinition>get("processDefinition").get("id"), processDefinition.getId()),
            cb.equal(token.<ProcessInstance>get("processInstance").<Long>get("id"), processInstance.<Long>get("id")),
            cb.equal(token.<Node>get("node").<Long>get("id"), taskNode.<Long>get("id")),
            cb.isNull(processInstance.get("end")),
            cb.isNull(token.get("end")),
            cb.equal(event.get("id"), createTimeAction.<Event>get("event").get("id")),
            cb.exists(subquery)
        );
        cq.multiselect(token, createTimeAction, taskNode).distinct(true);
        List<Object[]> resultList = entityManager.createQuery(cq).getResultList();
        for (Object[] result : resultList) {
            Token tokenResult = (Token) result[0];
            CreateTimerAction createTimerActionResult = (CreateTimerAction) result[1];
            TaskNode taskNodeResult = (TaskNode) result[2];
            ExecutionContext executionContext = new ExecutionContext(tokenResult);
            executionContext.setEventSource(taskNodeResult);
            createTimerActionResult.execute(executionContext);
        }
    }
    
    public ProcessDefinition findLatestProcessDefinition(String name) {
      return EntityManagerProducer.getEntityManager()
              .createNamedQuery("GraphSession.findLatestProcessDefinitionQuery", ProcessDefinition.class)
              .setParameter("name", name).setMaxResults(1).getSingleResult();
    }
    
    public List<String> getProcessDefinitionNames() {
        EntityManager entityManager = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<ProcessDefinition> processDefinition = cq.from(ProcessDefinition.class);
        cq.select(processDefinition.<String>get("name")).distinct(true);
        cq.orderBy(cb.asc(processDefinition.<String>get("name")));
        return entityManager.createQuery(cq).getResultList();
    }
    
    public static Number getProcessDefinitionId(String processDefinitionName) {
        EntityManager entityManager = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Number> cq = cb.createQuery(Number.class);
        Root<ProcessDefinition> processDefinition = cq.from(ProcessDefinition.class);
        cq.select(cb.max(processDefinition.<Number>get("id")));
        cq.where(
            cb.equal(processDefinition.<String>get("name"), cb.literal(processDefinitionName))
        );
        List<Number> result = entityManager.createQuery(cq).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Factory(value = "jbpmMessages", scope = ScopeType.APPLICATION)
    public Map<String, String> getMessages() {
        return getJbpmMessages();
    }

    public static synchronized Map<String, String> getJbpmMessages() {
        if (messagesMap == null) {
            messagesMap = new HashMap<String, String>();
            List<JbpmVariavelLabel> l = genericManager().findAll(JbpmVariavelLabel.class);
            for (JbpmVariavelLabel j : l) {
                messagesMap.put(j.getNomeVariavel(), j.getLabelVariavel());
            }
        }
        return messagesMap;
    }

    @SuppressWarnings(UNCHECKED)
    public static <T> T getProcessVariable(String name) {
        ProcessInstance processInstance = org.jboss.seam.bpm.ProcessInstance.instance();
        if (processInstance != null) {
            ContextInstance contextInstance = processInstance.getContextInstance();
            return (T) contextInstance.getVariable(name);
        }
        return null;
    }

    public static void setProcessVariable(String name, Object value) {
        getConxtextInstance().setVariable(name, value);
    }

    public static void createProcessVariable(String name, Object value) {
        getConxtextInstance().createVariable(name, value);
    }

    private static ContextInstance getConxtextInstance() {
        return org.jboss.seam.bpm.ProcessInstance.instance().getContextInstance();
    }

    public Object getConteudo(VariableAccess var, TaskInstance taskInstance) {
        String type = var.getMappedName().split(":")[0];
        Object variable = taskInstance.getVariable(var.getMappedName());
        
        if (isTypeEditor(type)) {
            Integer id = (Integer) variable;
            if (id != null) {
                Documento documento = documentoManager().find(id);
                if (documento == null) {
                    LOG.warn("Documento não encontrado: " + id);
                } else {
                    variable = documento.getDocumentoBin().getModeloDocumento();
                }
            }
        } else if (VariableType.BOOLEAN.name().equals(type)) {
            variable = Boolean.valueOf(variable.toString()) ? "Sim" : "Não";
        } else if (VariableType.MONETARY.name().equalsIgnoreCase(type)) {
            variable = String.format(FloatFormatConstants.F2, variable);
        } else {
            variable = variable.toString();
        }
        return variable;
    }

    public static JbpmUtil instance() {
        return (JbpmUtil) ComponentUtil.getComponent(NAME, ScopeType.APPLICATION);
    }

    public static GraphSession getGraphSession() {
        return new GraphSession(getJbpmSession());
    }

    public static Processo getProcesso() {
        Long idProcesso = JbpmUtil.getProcessVariable("processo");
        return idProcesso != null ? processoManager().find(idProcesso.intValue()) : null;
    }

    public static boolean isTypeEditor(String type) {
        return VariableType.EDITOR.name().equals(type);
    }

    public static List<Token> getTokensOfAutomaticNodesNotEnded() {
        EntityManager entityManager = EntityManagerProducer.getEntityManager();
        return entityManager.createQuery(JbpmQueries.TOKENS_OF_AUTOMATIC_NODES_NOT_ENDED_QUERY, Token.class).getResultList();
    }
    
    private DocumentoManager documentoManager() {
        return ComponentUtil.getComponent(DocumentoManager.NAME);
    }

    private static ProcessoManager processoManager() {
        return ComponentUtil.getComponent(ProcessoManager.NAME);
    }

    private static GenericManager genericManager() {
        return ComponentUtil.getComponent(GenericManager.NAME);
    }

    private static UsuarioTaskInstanceManager usuarioTaskInstanceManager() {
        return ComponentUtil.getComponent(UsuarioTaskInstanceManager.NAME);
    }
}
