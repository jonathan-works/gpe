package br.com.infox.epp.tarefa.manager;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.tarefa.dao.TarefaDAO;
import br.com.infox.epp.tarefa.entity.Tarefa;

@Stateless
@AutoCreate
@Name(TarefaManager.NAME)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TarefaManager extends Manager<TarefaDAO, Tarefa> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tarefaManager";

    public List<SelectItem> getPreviousNodes(String nodeKey) {
        return getDao().getPreviousNodes(nodeKey);
    }

    public void encontrarNovasTarefas() throws DAOException {
        getDao().encontrarNovasTarefas();
    }

    public Tarefa getTarefa(long idJbpmTask) {
        return getDao().getTarefa(idJbpmTask);
    }

    public Tarefa getTarefa(String tarefa, String fluxo) {
        return getDao().getTarefa(tarefa, fluxo);
    }

}
