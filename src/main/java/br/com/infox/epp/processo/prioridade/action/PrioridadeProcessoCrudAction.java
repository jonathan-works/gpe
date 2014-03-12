package br.com.infox.epp.processo.prioridade.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;
import br.com.infox.epp.processo.prioridade.manager.PrioridadeProcessoManager;

@Name(PrioridadeProcessoCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class PrioridadeProcessoCrudAction extends AbstractCrudAction<PrioridadeProcesso, PrioridadeProcessoManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "prioridadeProcessoCrudAction";

}
