package br.com.infox.epp.processo.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;

@Name(ProcessoEpaAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoEpaAction extends AbstractAction<ProcessoEpa, ProcessoEpaManager> {
    public static final String NAME = "processoEpaAction";

    public boolean alternarContabilizar(ProcessoEpa processoEpa) {
        return UPDATED.equals(update(processoEpa));
    }

}
