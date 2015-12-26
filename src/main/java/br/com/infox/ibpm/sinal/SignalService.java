package br.com.infox.ibpm.sinal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.jbpm.graph.def.Event;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.persistence.DAOException;
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
    public void movimentarTarefasListener(Long processInstanceId, String eventType) throws DAOException {
        List<SignalNodeBean> tarefas = getListeners(processInstanceId, eventType);
        for (SignalNodeBean tarefa : tarefas) {
            processoManager.movimentarProcessoJBPM(tarefa.getNodeId(), tarefa.getListenerConfiguration().getTransitionKey());
        }
    }
    
    public List<SignalNodeBean> getListeners(Long processInstanceId, String eventType) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SignalNodeBean> query = cb.createQuery(SignalNodeBean.class);
        Root<TaskInstance> taskInstance = query.from(TaskInstance.class);
        Join<TaskInstance, Task> task = taskInstance.join("task");
        Join<Task, Event> event = task.join("events");
        query.select(cb.construct(SignalNodeBean.class, taskInstance.get("id"), event.get("configuration")));
        
        List<Long> processInstanceIds = getAllProcessInstanceIds(processInstanceId);
        
        query.where(
            cb.isNull(taskInstance.get("end")),
            cb.isFalse(taskInstance.<Boolean>get("isSuspended")),
            cb.isTrue(taskInstance.<Boolean>get("isOpen")),
            cb.equal(event.get("eventType"), eventType),
            taskInstance.get("processInstance").<Long>get("id").in(processInstanceIds)
        );
        
        return entityManager.createQuery(query).getResultList();
    }
    
    private void listSubprocessInstanceIds(List<Long> subProcessInstanceParentIds, List<Long> subProcessInstanceIds) {
        subProcessInstanceIds.addAll(subProcessInstanceParentIds);
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Token> token = query.from(Token.class);
        query.where(
            cb.isNotNull(token.get("subProcessInstance")),
            token.get("processInstance").get("id").in(subProcessInstanceParentIds)
        );
        query.select(token.get("subProcessInstance").<Long>get("id"));
        subProcessInstanceParentIds = entityManager.createQuery(query).getResultList();
        if (!subProcessInstanceParentIds.isEmpty()) {
            listSubprocessInstanceIds(subProcessInstanceParentIds, subProcessInstanceIds);
        }
    }
    
    public List<Long> getAllProcessInstanceIds(Long parentProcessInstanceId) {
        List<Long> ids = new ArrayList<>();
        listSubprocessInstanceIds(Arrays.asList(parentProcessInstanceId), ids);
        return ids;
    }
    
    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }

}
