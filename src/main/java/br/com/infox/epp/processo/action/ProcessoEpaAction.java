package br.com.infox.epp.processo.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.AbstractAction;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.tarefa.manager.ProcessoEpaTarefaManager;

@Name(ProcessoEpaAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoEpaAction extends AbstractAction<ProcessoEpa> {
	public static final String NAME = "processoEpaAction";
	
	@In private ProcessoEpaTarefaManager processoEpaTarefaManager;
	
	public boolean alternarContabilizar(ProcessoEpa processoEpa) {
		return UPDATED.equals(update(processoEpa));
	}
	
}
