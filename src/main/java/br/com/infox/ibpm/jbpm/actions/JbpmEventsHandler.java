package br.com.infox.ibpm.jbpm.actions;

import java.io.Serializable;

import javax.persistence.TransactionRequiredException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.localizacao.manager.ProcessoLocalizacaoIbpmManager;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.tarefa.manager.TarefaJbpmManager;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.jbpm.ProcessBuilder;
import br.com.itx.exception.ApplicationException;
import br.com.itx.util.ComponentUtil;

@Name(JbpmEventsHandler.NAME)
@Install(precedence=Install.FRAMEWORK)
@Scope(ScopeType.EVENT)
public class JbpmEventsHandler implements Serializable {

	private static final String BPM = "BPM";
    private static final String JBPM_EVENTS_HANDLER = "JbpmEventsHandler";
    private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(JbpmEventsHandler.class);

	public static final String NAME = "jbpmEventsHandler";
	
	@Observer(Event.EVENTTYPE_TASK_END)
    public void removerProcessoLocalizacao(ExecutionContext context) {
        try {
        	Long taskId = context.getTask().getId();
            Long processId = context.getProcessInstance().getId();
            getProcessoLocalizacaoIbpmManager().deleteByTaskIdAndProcessId(taskId, processId);
        } catch (IllegalStateException e) {
            throwErroAoTentarRemoverProcessoLocalizacao(e);
        } catch (IllegalArgumentException e) {
        	 throwErroAoTentarRemoverProcessoLocalizacao(e);
        } catch (TransactionRequiredException e) {
        	 throwErroAoTentarRemoverProcessoLocalizacao(e);
        }
    }

	private void throwErroAoTentarRemoverProcessoLocalizacao(Exception e) {
		String action = "Remover o processo da localizacao: ";
		LOG.warn(action, e);
		throw new ApplicationException(ApplicationException.createMessage(
		        action + e.getLocalizedMessage(),
		        "removerProcessoLocalizacao()", JBPM_EVENTS_HANDLER, BPM));
	}

	@Observer(Event.EVENTTYPE_TASK_END)
	@End(beforeRedirect=true)
    public void refreshPainel(ExecutionContext context) {
        context.getTaskInstance().setActorId(null);
        try {
            getProcessoManager().apagarActorIdDoProcesso(JbpmUtil.getProcesso());
        } catch (IllegalStateException e) {
            throwErroAoLimparVariaveisDoPainel(e);
        } catch (IllegalArgumentException e) {
        	throwErroAoLimparVariaveisDoPainel(e);
        } catch (TransactionRequiredException e) {
        	throwErroAoLimparVariaveisDoPainel(e);
        }
    }

	private void throwErroAoLimparVariaveisDoPainel(Exception e) {
		String action = "Limpar as variáveis do painel para atualização: ";
		LOG.error(action, e);
		throw new ApplicationException(ApplicationException.createMessage(
		        action + e.getLocalizedMessage(), "refreshPainel()",
		        JBPM_EVENTS_HANDLER, BPM));
	}
	
	/**
	 * Atualiza o dicionário de Tarefas (tb_tarefa) com seus respectivos id's 
	 * de todas as versões.
	 **/
	@Observer(ProcessBuilder.POST_DEPLOY_EVENT)
    public static void updatePostDeploy() {
        try {
            getProcessoManager().atualizarProcessos();
            getTarefaManager().encontrarNovasTarefas();
            getTarefaJbpmManager().inserirVersoesTarefas();
        } catch (IllegalStateException e) {
            throwErroAoRealizarAtualizacaoAutomatica(e);
        } catch (TransactionRequiredException e) {
        	throwErroAoRealizarAtualizacaoAutomatica(e);
        }
    }

	private static void throwErroAoRealizarAtualizacaoAutomatica(Exception e) {
		String action = "Realizar atualização automáticas após publicação do fluxo: ";
		LOG.error(action, e);
		throw new ApplicationException(ApplicationException.createMessage(
		        action + e.getLocalizedMessage(), "updatePostDeploy()",
		        JBPM_EVENTS_HANDLER, BPM));
	}
	
	/**
	 * Antes de terminar a tarefa, remove a caixa do processo
	 * @param transition
	 */ 
	@Observer(Event.EVENTTYPE_TASK_END)
    public void removeCaixaProcesso(ExecutionContext context) {
        try {
        	Processo processo = JbpmUtil.getProcesso();
            getProcessoManager().removerProcessoDaCaixaAtual(processo);
        } catch (IllegalStateException ise) {
            throwErroAoTentarRemoverDaCaixa(ise);
        } catch (TransactionRequiredException tre) {
            throwErroAoTentarRemoverDaCaixa(tre);
        }
    }

	private void throwErroAoTentarRemoverDaCaixa(Exception e) {
		String action = "Remover o processo da caixa: ";
		LOG.warn(action, e);
		throw new ApplicationException(ApplicationException.createMessage(
		        action + e.getLocalizedMessage(), "removeCaixaProcesso()",
		        JBPM_EVENTS_HANDLER, BPM));
	}
	
	/**
	 * Retorna a instancia da classe JbpmEventsHandler
	 * @return
	 */
	public static JbpmEventsHandler instance() {
		return ComponentUtil.getComponent(JbpmEventsHandler.NAME);
	}
	
	private static TarefaManager getTarefaManager(){
		return ComponentUtil.getComponent(TarefaManager.NAME);
	}
	
	private static ProcessoLocalizacaoIbpmManager getProcessoLocalizacaoIbpmManager() {
		return ComponentUtil.getComponent(ProcessoLocalizacaoIbpmManager.NAME);
	}
	
	private static TarefaJbpmManager getTarefaJbpmManager(){
		return ComponentUtil.getComponent(TarefaJbpmManager.NAME);
	}
	
	private static ProcessoManager getProcessoManager(){
		return ComponentUtil.getComponent(ProcessoManager.NAME);
	}
	
}