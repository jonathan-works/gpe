package br.com.infox.epp.tarefa.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.entity.TarefaJbpm;
import br.com.infox.epp.tarefa.entity.TarefaJbpm_;
import br.com.infox.epp.tarefa.entity.Tarefa_;

@Stateless
@AutoCreate
@Name(TarefaJbpmDAO.NAME)
public class TarefaJbpmDAO extends DAO<TarefaJbpm> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tarefaJbpmDAO";

    public void inserirVersoesTarefas() throws DAOException {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<Tuple> query = cb.createTupleQuery();
    	Root<Tarefa> tarefa = query.from(Tarefa.class);
    	Root<Task> task = query.from(Task.class);
    	Join<Tarefa, Fluxo> fluxo = tarefa.join(Tarefa_.fluxo, JoinType.INNER);
    	Join<Task, ProcessDefinition> processDefinition = task.join("processDefinition", JoinType.INNER);
    	
    	Subquery<Integer> subquery = query.subquery(Integer.class);
    	Root<TarefaJbpm> tarefaJbpm = subquery.from(TarefaJbpm.class);
    	subquery.where(cb.equal(tarefaJbpm.get(TarefaJbpm_.tarefa), tarefa), cb.equal(tarefaJbpm.get(TarefaJbpm_.idJbpmTask), task.get("id")));
    	subquery.select(cb.literal(1));
    	
    	query.where(
    		cb.equal(fluxo.get(Fluxo_.fluxo), processDefinition.get("name")),
    		cb.equal(task.get("name"), tarefa.get(Tarefa_.tarefa)),
    		cb.exists(subquery).not()
    	);
    	query.multiselect(tarefa.alias("tarefa"), task.get("id").alias("idJbpmTask"));
    	
    	List<Tuple> result = getEntityManager().createQuery(query).getResultList();
    	for (Tuple tuple : result) {
    		TarefaJbpm tj = new TarefaJbpm();
    		tj.setIdJbpmTask(tuple.get("idJbpmTask", Long.class));
    		tj.setTarefa(tuple.get("tarefa", Tarefa.class));
    		persistWithoutFlush(tj);
    	}
    	flush();
    }

}
