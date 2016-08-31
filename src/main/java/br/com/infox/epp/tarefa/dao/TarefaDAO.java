package br.com.infox.epp.tarefa.dao;

import static br.com.infox.epp.tarefa.query.TarefaQuery.FLUXO_PARAM;
import static br.com.infox.epp.tarefa.query.TarefaQuery.ID_JBPM_TASK_PARAM;
import static br.com.infox.epp.tarefa.query.TarefaQuery.TAREFA_BY_ID_JBPM_TASK;
import static br.com.infox.epp.tarefa.query.TarefaQuery.TAREFA_BY_TAREFA_AND_FLUXO;
import static br.com.infox.epp.tarefa.query.TarefaQuery.TAREFA_PARAM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.entity.Tarefa_;

@Stateless
@AutoCreate
@Name(TarefaDAO.NAME)
public class TarefaDAO extends DAO<Tarefa> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tarefaDAO";
    
    @Inject
    private FluxoManager fluxoManager;
    
    public String getTaskName(String taskKey) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Node> node = cq.from(Node.class);
        cq.select(node.<String>get("name"));
        
        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<Node> node2 = subquery.from(Node.class);
        subquery.select(cb.max(node2.<Long>get("id")));
        subquery.where(
                cb.equal(node2.<String>get("key"), cb.literal(taskKey))
        );
        
        cq.where(
                cb.equal(subquery, node.<Long>get("id"))
        );
        
        return getEntityManager().createQuery(cq).getSingleResult();
    }

    public List<SelectItem> getPreviousNodes(String nodeKey) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SelectItem> cq = cb.createQuery(SelectItem.class);
        Root<Transition> transition = cq.from(Transition.class);
        Join<Transition, Node> nodeFrom = transition.join("from", JoinType.INNER);
        Join<Transition, Node> nodeTo = transition.join("to", JoinType.INNER);
        
        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<Node> node = subquery.from(Node.class);
        subquery.select(cb.max(node.<Long>get("id")));
        subquery.where(
                cb.equal(node.<String>get("key"), cb.literal(nodeKey))
        );
        
        cq.select(cb.construct(SelectItem.class, nodeFrom.<String>get("key"), nodeFrom.<String>get("name")));
        cq.where(
                cb.equal(subquery, nodeTo.<Long>get("id"))
        );
        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     * Popula a tabela tb_tarefa com todas as tarefas de todos os fluxos,
     * considerando como chave o nome da tarefa task.name_
     * @throws DAOException 
     */
    public void encontrarNovasTarefas() throws DAOException {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<Tuple> query = cb.createTupleQuery();
    	Root<Task> task = query.from(Task.class);
    	Root<Fluxo> fluxo = query.from(Fluxo.class);
    	Join<Task, ProcessDefinition> processDefinition = task.join("processDefinition", JoinType.INNER);
    	
    	Subquery<Integer> subquery = query.subquery(Integer.class);
    	Root<Tarefa> tarefa = subquery.from(Tarefa.class);
    	subquery.where(cb.equal(tarefa.get(Tarefa_.tarefa), task.get("name")), cb.equal(tarefa.get(Tarefa_.fluxo), fluxo));
    	subquery.select(cb.literal(1));
    	
    	query.where(
    		cb.equal(fluxo.get(Fluxo_.fluxo), processDefinition.get("name")),
    		cb.exists(subquery).not()
    	);
    	query.groupBy(fluxo.get(Fluxo_.idFluxo), task.get("name"));
    	query.multiselect(fluxo.get(Fluxo_.idFluxo).alias("idFluxo"), task.get("name").alias("taskName"));
    	
    	List<Tuple> result = getEntityManager().createQuery(query).getResultList();
    	for (Tuple tuple : result) {
    		Tarefa t = new Tarefa();
    		t.setFluxo(fluxoManager.find(tuple.get("idFluxo")));
    		t.setTarefa(tuple.get("taskName", String.class));
    		persistWithoutFlush(t);
    	}
    	flush();
    }

    public Tarefa getTarefa(long idJbpmTask) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ID_JBPM_TASK_PARAM, idJbpmTask);
        return getNamedSingleResult(TAREFA_BY_ID_JBPM_TASK, parameters);
    }

    public Tarefa getTarefa(String tarefa, String fluxo) {
        if (tarefa == null || fluxo == null) {
            return null;
        }
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(TAREFA_PARAM, tarefa);
        parameters.put(FLUXO_PARAM, fluxo);
        return getNamedSingleResult(TAREFA_BY_TAREFA_AND_FLUXO, parameters);
    }
}
