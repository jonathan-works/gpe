package br.com.infox.ibpm.sinal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SignalService {

    @Inject
    private SignalDao signalDao;
    @Inject
    private ProcessoManager processoManager;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persist(Signal signal) {
        signalDao.persist(signal);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(Signal signal) {
        signalDao.update(signal);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void dispatch(Integer idProcesso, String eventType) {
        Processo processo = processoManager.find(idProcesso);
        ProcessInstance processInstance = ManagedJbpmContext.instance().getProcessInstance(processo.getIdJbpm());
        ExecutionContext executionContext = new ExecutionContext(processInstance.getRootToken());
        ExecutionContext.pushCurrentContext(executionContext);
        try {
            dispatch(eventType, executionContext);
        } finally {
            ExecutionContext.popCurrentContext(executionContext);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void dispatch(String eventType) {
        ExecutionContext executionContext = ExecutionContext.currentExecutionContext();
        dispatch(eventType, executionContext);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void dispatch(String eventType, ExecutionContext executionContext) {
        ProcessInstance currentProcessInstance = executionContext.getProcessInstance().getRoot();
        eventType = Event.getListenerEventType(eventType);
        startStartStateListeningImpl(eventType, getSignalParams());
        endSubprocessListening(currentProcessInstance, eventType);
        movimentarTarefasListener(currentProcessInstance.getId(), eventType);        	        	
    }
    
    private void endSubprocessListening(ProcessInstance processInstance, String eventType) {
        Processo processo = processoManager.getProcessoByIdJbpm(processInstance.getId());
        while (processo != null) {
            List<Long> subprocessInstanceIds = getSubprocessInstanceIds(Arrays.asList(processo.getIdJbpm()));
            while (!subprocessInstanceIds.isEmpty()) {
                List<SignalNodeBean> signalNodes = getSubprocessListening(subprocessInstanceIds, eventType);
                for (SignalNodeBean signalNodeBean : signalNodes) {
                    if (signalNodeBean.canExecute()) {
                        processoManager.cancelJbpmSubprocess(signalNodeBean.getId(), signalNodeBean.getListenerConfiguration().getTransitionKey());
                    }
                }
                subprocessInstanceIds = getSubprocessInstanceIds(subprocessInstanceIds);
            }
            processo = processo.getProcessoPai();
        }
    }
    
    private void movimentarTarefasListener(Long processInstanceId, String eventType) throws DAOException {
        Processo processo = processoManager.getProcessoByIdJbpm(processInstanceId);
        while (processo != null) {
            List<SignalNodeBean> signalNodes = getTasksListening(processo.getIdJbpm(), eventType);
            for (SignalNodeBean signalNodeBean : signalNodes) {
                ExecutionContext.currentExecutionContext().setTaskInstance(ManagedJbpmContext.instance().getTaskInstanceForUpdate(signalNodeBean.getId()));
                if (signalNodeBean.canExecute()) {
                    processoManager.movimentarProcessoJBPM(signalNodeBean.getId(), signalNodeBean.getListenerConfiguration().getTransitionKey());
                }
            }
            processo = processo.getProcessoPai();
        }
    }
    
    public List<ProcessInstance> startStartStateListening(String eventType, List<SignalParam> params) {
        eventType = Event.getListenerEventType(eventType);
        ExecutionContext executionContext = createExecutionContext(params);
        ExecutionContext.pushCurrentContext(executionContext);
        try {
            return startStartStateListeningImpl(eventType, params);    	
        } finally {
            ExecutionContext.popCurrentContext(executionContext);
        }
    }
    
    private ExecutionContext createExecutionContext(List<SignalParam> params) {
        Token token = new Token();
        token.setProcessInstance(new ProcessInstance());
        ExecutionContext executionContext = new ExecutionContext(token);
        ContextInstance contextInstance = executionContext.getContextInstance();
        for (SignalParam signalParam : params) {
            contextInstance.setTransientVariable(signalParam.getName(), signalParam.getValue());
        }
        return executionContext;
    }

    private List<ProcessInstance> startStartStateListeningImpl(String eventType, List<SignalParam> params) {
        List<SignalNodeBean> signalNodes = getStartStateListening(eventType);
        List<ProcessInstance> processInstances = new ArrayList<>();
        for (SignalNodeBean signalNodeBean : signalNodes) {
            if (signalNodeBean.canExecute()) {
                ProcessDefinition processDefinition = getEntityManager().find(ProcessDefinition.class, signalNodeBean.getId());
                ProcessInstance newProcessInstance = processoManager.startJbpmProcess(processDefinition.getName(), signalNodeBean.getListenerConfiguration().getTransitionKey(), params);
                processInstances.add(newProcessInstance);
            }
        }
        
        return processInstances;
    }
    
    private List<SignalParam> getSignalParams() {
    	ExecutionContext ctx = ExecutionContext.currentExecutionContext();
        if (ctx == null) {
            return Collections.emptyList();
        }
    	Action action = ctx.getAction();
        if (action == null) {
            return Collections.emptyList();
        }
        Event event = action.getRoot().getEvent();
        if (event == null) {
            return Collections.emptyList();
        } else {
            DispatcherConfiguration dispatcherConfiguration = DispatcherConfiguration.fromJson(event.getConfiguration());
            return dispatcherConfiguration.getSignalParams() == null ? Collections.<SignalParam>emptyList() : dispatcherConfiguration.getSignalParams();
        }
    }

    private List<SignalNodeBean> getStartStateListening(String eventType) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SignalNodeBean> cq = cb.createQuery(SignalNodeBean.class);
        Root<ProcessDefinition> definition = cq.from(ProcessDefinition.class);
        Join<ProcessDefinition, Node> node = definition.join("startState", JoinType.INNER);
        Join<Node, Event> event = node.join("events", JoinType.INNER);
        cq.select(cb.construct(SignalNodeBean.class, definition.get("id"), event.get("configuration")));
        
        Subquery<Integer> versionQuery = cq.subquery(Integer.class);
        Root<ProcessDefinition> from = versionQuery.from(ProcessDefinition.class);
        versionQuery.select(cb.max(from.<Integer>get(("version"))));
        versionQuery.groupBy(from.get("name"));
        versionQuery.having(cb.equal(from.get("name"), definition.get("name")));
        
        cq.where(
            cb.equal(event.get("eventType"), eventType),
            cb.equal(definition.get("version"), versionQuery),
            cb.equal(node.type(), StartState.class)
        );
        return getEntityManager().createQuery(cq).getResultList();
    }

    private List<SignalNodeBean> getSubprocessListening(List<Long> subprocessInstanceIds, String eventType) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SignalNodeBean> cq = cb.createQuery(SignalNodeBean.class);
        Root<ProcessInstance> process = cq.from(ProcessInstance.class);
        Join<ProcessInstance, Token> token = process.join("superProcessToken", JoinType.INNER);
        Join<Token, Node> node = token.join("node", JoinType.INNER);
        Join<Node, Event> event = node.join("events", JoinType.INNER);
        cq.select(cb.construct(SignalNodeBean.class, process.get("id"), event.get("configuration")));
        cq.where(
            process.get("id").in(subprocessInstanceIds),
            cb.isNotNull(token.get("nodeEnter")),
            cb.isNull(token.get("end")),
            cb.equal(event.get("eventType"), eventType),
            cb.isFalse(token.<Boolean>get("isSuspended")),
            cb.isNull(process.get("end")),
            cb.equal(node.type(), ProcessState.class)
        );
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    private List<SignalNodeBean> getTasksListening(Long processInstanceId, String eventType) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SignalNodeBean> query = cb.createQuery(SignalNodeBean.class);
        Root<TaskInstance> taskInstance = query.from(TaskInstance.class);
        Join<TaskInstance, ProcessInstance> process = taskInstance.join("processInstance", JoinType.INNER);
        Join<TaskInstance, Task> task = taskInstance.join("task", JoinType.INNER);
        Join<Task, TaskNode> taskNode = task.join("taskNode", JoinType.INNER);
        Join<TaskNode, Event> event = taskNode.join("events", JoinType.INNER);
        query.select(cb.construct(SignalNodeBean.class, taskInstance.get("id"), event.get("configuration")));
        
        List<Long> processInstanceIds = getAllProcessInstanceIds(processInstanceId);
        
        query.where(
            cb.isNull(taskInstance.get("end")),
            cb.isFalse(taskInstance.<Boolean>get("isSuspended")),
            cb.isTrue(taskInstance.<Boolean>get("isOpen")),
            cb.equal(event.get("eventType"), eventType),
            cb.isNull(process.get("end")),
            cb.equal(taskNode.type(), TaskNode.class),
            taskInstance.get("processInstance").<Long>get("id").in(processInstanceIds)
        );
        return getEntityManager().createQuery(query).getResultList();
    }
    
    private void listSubprocessInstanceIds(List<Long> subProcessInstanceParentIds, List<Long> subProcessInstanceIds) {
        subProcessInstanceIds.addAll(subProcessInstanceParentIds);
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Token> token = query.from(Token.class);
        Join<Token, ProcessInstance> process = token.join("processInstance", JoinType.INNER);
        query.where(
            cb.isNotNull(token.get("subProcessInstance")),
            cb.isNull(process.get("end")),
            process.get("id").in(subProcessInstanceParentIds)
        );
        query.select(token.get("subProcessInstance").<Long>get("id"));
        subProcessInstanceParentIds = entityManager.createQuery(query).getResultList();
        if (!subProcessInstanceParentIds.isEmpty()) {
            listSubprocessInstanceIds(subProcessInstanceParentIds, subProcessInstanceIds);
        }
    }
    
    private List<Long> getSubprocessInstanceIds(List<Long> parentProcessInstanceIds) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Token> token = query.from(Token.class);
        Join<Token, ProcessInstance> process = token.join("processInstance", JoinType.INNER);
        query.where(
            cb.isNotNull(token.get("subProcessInstance")),
            cb.isNull(process.get("end")),
            process.get("id").in(parentProcessInstanceIds)
        );
        query.select(token.get("subProcessInstance").<Long>get("id"));
        return getEntityManager().createQuery(query).getResultList();
    }
    
    private List<Long> getAllProcessInstanceIds(Long parentProcessInstanceId) {
        List<Long> ids = new ArrayList<>();
        listSubprocessInstanceIds(Arrays.asList(parentProcessInstanceId), ids);
        return ids;
    }
    
    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
}
