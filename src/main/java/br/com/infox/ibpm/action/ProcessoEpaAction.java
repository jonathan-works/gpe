package br.com.infox.ibpm.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.epp.entity.ProcessoEpa;

@Name(ProcessoEpaAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoEpaAction extends AbstractAction {
	public static final String NAME = "processoEpaAction";
	
	public boolean alternarContabilizar(ProcessoEpa processoEpa) {
		processoEpa.setContabilizar(!processoEpa.getContabilizar());
		return AbstractAction.PERSISTED.equals(persist(processoEpa));
	}
	
}
