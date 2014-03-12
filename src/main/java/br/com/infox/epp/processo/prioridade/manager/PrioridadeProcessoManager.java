package br.com.infox.epp.processo.prioridade.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.prioridade.dao.PrioridadeProcessoDAO;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;

@Name(PrioridadeProcessoManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class PrioridadeProcessoManager extends Manager<PrioridadeProcessoDAO, PrioridadeProcesso> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "prioridadeProcessoManager";
}
