package br.com.infox.epp.tarefa.dao;

import static br.com.infox.epp.tarefa.query.TarefaQuery.FLUXO_PARAM;
import static br.com.infox.epp.tarefa.query.TarefaQuery.ID_JBPM_TASK_PARAM;
import static br.com.infox.epp.tarefa.query.TarefaQuery.NOVAS_TAREFAS;
import static br.com.infox.epp.tarefa.query.TarefaQuery.PARAM_ID_TAREFA;
import static br.com.infox.epp.tarefa.query.TarefaQuery.PREVIOUS_NODES;
import static br.com.infox.epp.tarefa.query.TarefaQuery.TAREFA_BY_ID_JBPM_TASK;
import static br.com.infox.epp.tarefa.query.TarefaQuery.TAREFA_BY_TAREFA_AND_FLUXO;
import static br.com.infox.epp.tarefa.query.TarefaQuery.TAREFA_PARAM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.tarefa.entity.Tarefa;

@Name(TarefaDAO.NAME)
@AutoCreate
public class TarefaDAO extends DAO<Tarefa> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tarefaDAO";

    public List<SelectItem> getPreviousNodes(Tarefa tarefa) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_TAREFA, tarefa.getIdTarefa());
        List<Object[]> list = getNamedResultList(PREVIOUS_NODES, parameters);
        List<SelectItem> previousTasksItems = new ArrayList<SelectItem>();
        previousTasksItems.add(new SelectItem(null, "Selecione a Tarefa Anterior"));
        for (Object[] obj : list) {
            previousTasksItems.add(new SelectItem(obj[0], obj[1].toString()));
        }
        return previousTasksItems;
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
