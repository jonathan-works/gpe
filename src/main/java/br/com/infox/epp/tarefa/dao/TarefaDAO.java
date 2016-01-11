package br.com.infox.epp.tarefa.dao;

import static br.com.infox.epp.tarefa.query.TarefaQuery.FLUXO_PARAM;
import static br.com.infox.epp.tarefa.query.TarefaQuery.ID_JBPM_TASK_PARAM;
import static br.com.infox.epp.tarefa.query.TarefaQuery.NOVAS_TAREFAS;
import static br.com.infox.epp.tarefa.query.TarefaQuery.TAREFA_BY_ID_JBPM_TASK;
import static br.com.infox.epp.tarefa.query.TarefaQuery.TAREFA_BY_TAREFA_AND_FLUXO;
import static br.com.infox.epp.tarefa.query.TarefaQuery.TAREFA_PARAM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.tarefa.entity.Tarefa;

@Name(TarefaDAO.NAME)
@AutoCreate
public class TarefaDAO extends DAO<Tarefa> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tarefaDAO";

    public List<SelectItem> getPreviousNodes(String nodeKey) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<SelectItem> cq = cb.createQuery(SelectItem.class);
        Root<Transition> transition = cq.from(Transition.class);
        Join<Transition, Node> nodeFrom = transition.join("from", JoinType.INNER);
        Join<Transition, Node> nodeTo = transition.join("to", JoinType.INNER);
        cq.select(cb.construct(SelectItem.class, nodeFrom.<Long>get("id"), nodeFrom.<String>get("name")));
        cq.where(
                cb.equal(nodeTo.get("key"), cb.literal(nodeKey))
        );
        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     * Popula a tabela tb_tarefa com todas as tarefas de todos os fluxos,
     * considerando como chave o nome da tarefa task.name_
     * @throws DAOException 
     */
    public void encontrarNovasTarefas() throws DAOException {
        executeNamedQueryUpdate(NOVAS_TAREFAS);
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
    
    @Override
    public Tarefa persist(Tarefa object) throws DAOException {
        throw new UnsupportedOperationException();
    }

}
