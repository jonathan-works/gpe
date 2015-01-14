package br.com.infox.ibpm.event;

import java.io.Serializable;

import javax.persistence.TransactionRequiredException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.localizacao.manager.ProcessoLocalizacaoIbpmManager;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.tarefa.manager.TarefaJbpmManager;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.ApplicationException;
import br.com.infox.seam.util.ComponentUtil;

@Name(JbpmEventsHandler.NAME)
@Install(precedence = Install.FRAMEWORK)
@Scope(ScopeType.EVENT)
public class JbpmEventsHandler implements Serializable {

    private static final String BPM = "BPM";
    private static final String JBPM_EVENTS_HANDLER = "JbpmEventsHandler";
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(JbpmEventsHandler.class);

    public static final String NAME = "jbpmEventsHandler";

    @Observer(Event.EVENTTYPE_TASK_END)
    public void removerProcessoLocalizacao(ExecutionContext context) throws DAOException {
        try {
            Long taskId = context.getTask().getId();
            Long processId = context.getProcessInstance().getId();
            getProcessoLocalizacaoIbpmManager().deleteByTaskIdAndProcessId(taskId, processId);
        } catch (IllegalStateException | IllegalArgumentException | TransactionRequiredException exception) {
            String action = "Remover o processo da localizacao: ";
            LOG.warn(action, exception);
            throw new ApplicationException(ApplicationException.createMessage(action
                    + exception.getLocalizedMessage(), "removerProcessoLocalizacao()", JBPM_EVENTS_HANDLER, BPM), exception);
        }
    }
    
    /**
     * Atualiza o dicionário de Tarefas (tb_tarefa) com seus respectivos id's de
     * todas as versões.
     * @throws DAOException 
     **/
    @Observer(ProcessBuilder.POST_DEPLOY_EVENT)
    public static void updatePostDeploy() throws DAOException {
        try {
        	getProcessoManager().atualizarProcessos();
            getTarefaManager().encontrarNovasTarefas();
            getTarefaJbpmManager().inserirVersoesTarefas();
        } catch (IllegalStateException | TransactionRequiredException exception) {
            String action = "Realizar atualização automáticas após publicação do fluxo: ";
            LOG.error(action, exception);
            throw new ApplicationException(ApplicationException.createMessage(action
                    + exception.getLocalizedMessage(), "updatePostDeploy()", JBPM_EVENTS_HANDLER, BPM), exception);
        }
    }

    /**
     * Retorna a instancia da classe JbpmEventsHandler
     * 
     * @return
     */
    public static JbpmEventsHandler instance() {
        return ComponentUtil.getComponent(JbpmEventsHandler.NAME);
    }

    private static TarefaManager getTarefaManager() {
        return ComponentUtil.getComponent(TarefaManager.NAME);
    }

    private static ProcessoLocalizacaoIbpmManager getProcessoLocalizacaoIbpmManager() {
        return ComponentUtil.getComponent(ProcessoLocalizacaoIbpmManager.NAME);
    }

    private static TarefaJbpmManager getTarefaJbpmManager() {
        return ComponentUtil.getComponent(TarefaJbpmManager.NAME);
    }

    private static ProcessoManager getProcessoManager() {
        return ComponentUtil.getComponent(ProcessoManager.NAME);
    }

}
