package br.com.infox.epp.processo.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.action.AbstractAction;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.tarefa.manager.ProcessoEpaTarefaManager;

@Name(ProcessoEpaAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoEpaAction extends AbstractAction<ProcessoEpa> {
	public static final String NAME = "processoEpaAction";
	private static final Log LOG = Logging.getLog(ProcessoEpaAction.class);
	
	@In private ProcessoEpaTarefaManager processoEpaTarefaManager;
	
	public boolean alternarContabilizar(ProcessoEpa processoEpa) {
		return AbstractAction.UPDATED.equals(update(processoEpa));
	}
	
	public void atualizarFinalizadas() {
		try {
			processoEpaTarefaManager.updateTarefasFinalizadas();
		} catch (DAOException e) {
			LOG.error(".atualizarFinalizadas()", e);
			FacesMessages.instance().add(e.getLocalizedMessage());
		}
	}
	
}
