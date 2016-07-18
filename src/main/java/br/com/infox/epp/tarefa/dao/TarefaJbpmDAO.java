package br.com.infox.epp.tarefa.dao;

import static br.com.infox.epp.tarefa.query.TarefaJbpmQuery.INSERT_TAREFA_VERSIONS;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.tarefa.entity.TarefaJbpm;

@Stateless
@AutoCreate
@Name(TarefaJbpmDAO.NAME)
public class TarefaJbpmDAO extends DAO<TarefaJbpm> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tarefaJbpmDAO";

    public void inserirVersoesTarefas() throws DAOException {
        executeNamedQueryUpdate(INSERT_TAREFA_VERSIONS);
    }

}
