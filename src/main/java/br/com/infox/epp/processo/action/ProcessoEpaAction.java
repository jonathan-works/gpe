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
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoEpaAction";

    public boolean alternarContabilizar(ProcessoEpa processoEpa) {
        processoEpa.getSituacaoPrazo().toString();
        String update = update(processoEpa);
        return UPDATED.equals(update);
    }

}
