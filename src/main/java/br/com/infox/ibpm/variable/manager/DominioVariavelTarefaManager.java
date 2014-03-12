package br.com.infox.ibpm.variable.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.ibpm.variable.dao.DominioVariavelTarefaDAO;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;

@Name(DominioVariavelTarefaManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class DominioVariavelTarefaManager extends Manager<DominioVariavelTarefaDAO, DominioVariavelTarefa> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "dominioVariavelTarefaManager";
}
