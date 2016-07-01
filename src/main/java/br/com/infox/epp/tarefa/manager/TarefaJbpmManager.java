package br.com.infox.epp.tarefa.manager;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.tarefa.dao.TarefaJbpmDAO;
import br.com.infox.epp.tarefa.entity.TarefaJbpm;

@Stateless
@Name(TarefaJbpmManager.NAME)
@AutoCreate
public class TarefaJbpmManager extends Manager<TarefaJbpmDAO, TarefaJbpm> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tarefaJbpmManager";

    public void inserirVersoesTarefas() throws DAOException {
        getDao().inserirVersoesTarefas();
    }

}
