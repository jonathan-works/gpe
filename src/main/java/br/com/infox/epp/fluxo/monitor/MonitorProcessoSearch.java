package br.com.infox.epp.fluxo.monitor;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.cdi.producer.EntityManagerProducer;

@Stateless
public class MonitorProcessoSearch {

    public List<MonitorProcessoDTO> listByFluxo(Long processdefinition) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<MonitorProcessoDTO> cq = cb.createQuery(MonitorProcessoDTO.class);

        Root<TaskInstance> ti = cq.from(TaskInstance.class);
        Join<TaskInstance, Task> t = ti.join("task", JoinType.INNER);
        Join<Task, Node> n = t.join("taskNode", JoinType.INNER);

        cq.groupBy(n.get("id"));
        cq.select(cb.construct(MonitorProcessoDTO.class, n.<String>get("key"), cb.countDistinct(ti.get("processInstance").get("id"))));
        cq.where(cb.equal(t.<ProcessDefinition>get("processDefinition").<Long>get("id"), processdefinition),
                cb.isNull(ti.get("end")));

        return getEntityManager().createQuery(cq).getResultList();
    }

    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
}
