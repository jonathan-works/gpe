package br.com.infox.ibpm.task.dao;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.annotations.QueryHints;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.core.persistence.PersistenceController;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TaskInstanceSearch extends PersistenceController {
    
    @Inject @GenericDao
    private Dao<TaskInstance, Long> dao; 
    
    public String getAssignee(Long idTaskInstance) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<TaskInstance> taskInstance = cq.from(TaskInstance.class);
        cq.select(taskInstance.<String>get("assignee"));
        cq.where(cb.equal(taskInstance.<Long>get("id"), cb.literal(idTaskInstance)));
        return dao.getSingleResult(getEntityManager().createQuery(cq));
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TaskInstance findTaskInstanceByTokenId(Long tokenId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<TaskInstance> cq = cb.createQuery(TaskInstance.class);
        Root<TaskInstance> taskInstance = cq.from(TaskInstance.class);
        cq.select(taskInstance);
        cq.where(
           cb.equal(taskInstance.<Token>get("token").get("id"), cb.literal(tokenId)),
           cb.isFalse(taskInstance.<Boolean>get("isSuspended")),
           cb.isTrue(taskInstance.<Boolean>get("isOpen")),
           cb.isNull(taskInstance.<String>get("assignee"))
        );
        return dao.getSingleResult(getEntityManager().createQuery(cq).setHint(QueryHints.CACHEABLE, false));
    }
    
}
