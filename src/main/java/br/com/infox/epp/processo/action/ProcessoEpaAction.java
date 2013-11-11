package br.com.infox.epp.processo.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoEpaTarefaManager;

@Name(ProcessoEpaAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoEpaAction extends AbstractAction {
	public static final String NAME = "processoEpaAction";
	@In private ProcessoEpaTarefaManager processoEpaTarefaManager;
	
	public boolean alternarContabilizar(ProcessoEpa processoEpa) {
		return AbstractAction.UPDATED.equals(update(processoEpa));
	}
	
	public void atualizarFinalizadas() {
		processoEpaTarefaManager.updateTarefasFinalizadas();
	}
	
}
