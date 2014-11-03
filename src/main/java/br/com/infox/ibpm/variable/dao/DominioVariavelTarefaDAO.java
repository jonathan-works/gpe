package br.com.infox.ibpm.variable.dao;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.DAO;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(DominioVariavelTarefaDAO.NAME)
public class DominioVariavelTarefaDAO extends DAO<DominioVariavelTarefa> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "dominioVariavelTarefaDAO";

}
