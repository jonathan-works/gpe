package br.com.infox.ibpm.event;

import javax.enterprise.event.Observes;
import javax.persistence.TransactionRequiredException;

import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.bpm.cdi.qualifier.Events.TaskEnd;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.localizacao.manager.ProcessoLocalizacaoIbpmManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.ApplicationException;
import br.com.infox.seam.util.ComponentUtil;

public class JbpmEventsHandler {

    private static final String BPM = "BPM";
    private static final LogProvider LOG = Logging.getLogProvider(JbpmEventsHandler.class);

    public void removerProcessoLocalizacao(@Observes @TaskEnd ExecutionContext context) throws DAOException {
        try {
            Long taskId = context.getTask().getId();
            Long processId = context.getProcessInstance().getId();
            getProcessoLocalizacaoIbpmManager().deleteByTaskIdAndProcessId(taskId, processId);
        } catch (IllegalStateException | IllegalArgumentException | TransactionRequiredException exception) {
            String action = "Remover o processo da localizacao: ";
            LOG.warn(action, exception);
            throw new ApplicationException(ApplicationException.createMessage(action
                    + exception.getLocalizedMessage(), "removerProcessoLocalizacao()", JbpmEventsHandler.class.getSimpleName(), BPM), exception);
        }
    }

    private static ProcessoLocalizacaoIbpmManager getProcessoLocalizacaoIbpmManager() {
        return ComponentUtil.getComponent(ProcessoLocalizacaoIbpmManager.NAME);
    }

}
