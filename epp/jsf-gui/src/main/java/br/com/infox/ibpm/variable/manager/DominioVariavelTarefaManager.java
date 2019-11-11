package br.com.infox.ibpm.variable.manager;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.ibpm.variable.dao.DominioVariavelTarefaDAO;
import br.com.infox.ibpm.variable.entity.DominioVariavelTarefa;

@Stateless
@Name(DominioVariavelTarefaManager.NAME)
public class DominioVariavelTarefaManager extends Manager<DominioVariavelTarefaDAO, DominioVariavelTarefa> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "dominioVariavelTarefaManager";
    
}