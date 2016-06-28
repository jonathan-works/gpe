package br.com.infox.epp.fluxo.monitor;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;

@Stateless
public class MonitorProcessoSearch {

    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }

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

    public List<Fluxo> getFluxoList() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Fluxo> cq = cb.createQuery(Fluxo.class);
        Root<Fluxo> f = cq.from(Fluxo.class);
        cq.select(f);
        cq.where(cb.isTrue(f.get(Fluxo_.publicado)));

        return getEntityManager().createQuery(cq).getResultList();
    }

    public ProcessDefinition getProcessDefinitionByFluxo(Fluxo f) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProcessDefinition> cq = cb.createQuery(ProcessDefinition.class);
        Root<ProcessDefinition> pd = cq.from(ProcessDefinition.class);
        cq.select(pd);
        cq.where(cb.equal(pd.get("name"), f.getFluxo()));
        cq.orderBy(cb.desc(pd.get("version")));
        TypedQuery<ProcessDefinition> query = getEntityManager().createQuery(cq);
        List<ProcessDefinition> fluxoList = query.getResultList();
        return fluxoList != null && !fluxoList.isEmpty() ? fluxoList.get(-0) : null;
    }
}
