package br.com.infox.epp.processo.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;

@Name(ProcessoAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoAction extends AbstractAction<Processo, ProcessoManager> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoAction";

    public boolean alternarContabilizar(Processo processo) {
    	processo.getSituacaoPrazo().toString();
        String update = update(processo);
        return UPDATED.equals(update);
    }

}
