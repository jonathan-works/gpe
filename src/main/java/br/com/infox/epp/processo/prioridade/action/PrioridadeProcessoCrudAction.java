package br.com.infox.epp.processo.prioridade.action;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;
import br.com.infox.epp.processo.prioridade.manager.PrioridadeProcessoManager;

@Named
@ViewScoped
public class PrioridadeProcessoCrudAction extends AbstractCrudAction<PrioridadeProcesso, PrioridadeProcessoManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private PrioridadeProcessoManager prioridadeProcessoManager;

    @Override
    protected PrioridadeProcessoManager getManager() {
        return prioridadeProcessoManager;
    }
}
