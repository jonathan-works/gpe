package br.com.infox.epp.tarefa.dao;

import static br.com.infox.epp.tarefa.query.TarefaJbpmQuery.INSERT_TAREFA_VERSIONS;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;

@Name(TarefaJbpmDAO.NAME)
@AutoCreate
public class TarefaJbpmDAO extends GenericDAO {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "tarefaJbpmDAO";

    /**
     * Insere para cada tarefa na tabela de tb_tarefa todos os ids que esse já
     * possuiu.
     */
    public void inserirVersoesTarefas() {
        executeNamedQueryUpdate(INSERT_TAREFA_VERSIONS);
    }

}
