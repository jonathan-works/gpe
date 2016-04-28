package br.com.infox.ibpm.swimlane;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;

import br.com.infox.cdi.producer.EntityManagerProducer;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SwimlaneInstanceSearch {
    
    public List<SwimlaneInstance> getSwimlaneInstancesByProcessDefinition(Long processDefinitionId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SwimlaneInstance> cq = cb.createQuery(SwimlaneInstance.class);
        Root<SwimlaneInstance> swimlaneInstance = cq.from(SwimlaneInstance.class);
        swimlaneInstance.fetch("swimlane", JoinType.INNER);
        Join<SwimlaneInstance, TaskMgmtInstance> taskMgmtInstance = swimlaneInstance.join("taskMgmtInstance", JoinType.INNER);
        Join<TaskMgmtInstance, ProcessInstance> processInstance = taskMgmtInstance.join("processInstance", JoinType.INNER);
        
        cq.select(swimlaneInstance);
        cq.where(
            cb.equal(processInstance.<ProcessDefinition>get("processDefinition").get("id"), cb.literal(processDefinitionId)),
            cb.isNull(processInstance.<Date>get("end"))
        );
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    public EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }

}
