package br.com.infox.epp.processo.prioridade.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;

@Name(PrioridadeProcessoCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class PrioridadeProcessoCrudAction extends AbstractCrudAction <PrioridadeProcesso> {
    
    public static final String NAME = "prioridadeProcessoCrudAction";
    
}
