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

import org.jbpm.context.exe.variableinstance.StringInstance;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Stateless
public class MonitorProcessoSearch {

    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }

    public List<MonitorTarefaDTO> listTarefaHumanaByProcessDefinition(Long processDefinition) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<MonitorTarefaDTO> cq = cb.createQuery(MonitorTarefaDTO.class);

        Root<TaskInstance> ti = cq.from(TaskInstance.class);
        Join<TaskInstance, Task> t = ti.join("task", JoinType.INNER);
        Join<Task, Node> n = t.join("taskNode", JoinType.INNER);

        cq.groupBy(n.get("id"));
        cq.select(cb.construct(MonitorTarefaDTO.class, n.<String>get("key"), cb.countDistinct(ti.get("processInstance").get("id"))));
        cq.where(cb.equal(t.get("processDefinition").get("id"), processDefinition),
                cb.isNull(ti.get("end")));

        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<MonitorProcessoInstanceDTO> listInstanciasTarefaHumana(Long processDefinition) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<MonitorProcessoInstanceDTO> cq = cb.createQuery(MonitorProcessoInstanceDTO.class);

        Root<TaskInstance> ti = cq.from(TaskInstance.class);
        Join<TaskInstance, Task> task = ti.join("task", JoinType.INNER);
        Join<TaskInstance, Token> t = ti.join("token", JoinType.INNER);
        Root<StringInstance> vi = cq.from(StringInstance.class);

        cq.select(cb.construct(MonitorProcessoInstanceDTO.class, vi.get("value"), task.get("name"), ti.get("start"), cb.literal("OK")));
        cq.where(cb.equal(task.get("processDefinition").get("id"), processDefinition),
                cb.isNull(ti.get("end")),
                cb.equal(vi.get("token"), t),
                cb.equal(vi.get("name"), "numeroProcesso"));

        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<MonitorTarefaDTO> listNosAutomaticosByProcessDefinition(Long processDefinition) {
        String qlString = "select new br.com.infox.epp.fluxo.monitor.MonitorTarefaDTO(node.key, count(token.id)) "
                + "from org.jbpm.graph.exe.Token token "
                    + "inner join token.node node "
                + "where token.end IS NULL "
                    + "and token.lock is null "
                    + "and node.class IN ('N', 'M', 'D') "
                    + "and node.processDefinition.id = :idProcessDefinition "
                + "group by node.id";

        TypedQuery<MonitorTarefaDTO> typedQuery = getEntityManager().createQuery(qlString, MonitorTarefaDTO.class);
        typedQuery.setParameter("idProcessDefinition", processDefinition);
        return typedQuery.getResultList();
    }

    public List<MonitorProcessoInstanceDTO> listInstanciasNoAutomatico(long processDefinition) {
        String qlString = "select new br.com.infox.epp.fluxo.monitor.MonitorProcessoInstanceDTO(si.value, node.name, token.start, 'ERROR') "
                + "from org.jbpm.graph.exe.Token token, org.jbpm.context.exe.variableinstance.StringInstance si "
                    + "inner join token.node node "
                + "where token.end IS NULL "
                    + "and token.lock is null "
                    + "and node.class IN ('N', 'M', 'D') "
                    + "and node.processDefinition.id = :idProcessDefinition "
                    + "and si.token = token "
                    + "and si.name = 'numeroProcesso'";

        TypedQuery<MonitorProcessoInstanceDTO> typedQuery = getEntityManager().createQuery(qlString, MonitorProcessoInstanceDTO.class);
        typedQuery.setParameter("idProcessDefinition", processDefinition);
        return typedQuery.getResultList();
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
