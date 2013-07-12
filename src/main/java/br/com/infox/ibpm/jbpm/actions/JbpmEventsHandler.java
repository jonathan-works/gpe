package br.com.infox.ibpm.jbpm.actions;

import java.io.Serializable;

import javax.persistence.EntityManager;
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

import br.com.infox.epp.manager.ProcessoManager;
import br.com.infox.ibpm.entity.Processo;
import br.com.infox.ibpm.jbpm.JbpmUtil;
import br.com.infox.ibpm.jbpm.ProcessBuilder;
import br.com.infox.ibpm.manager.ProcessoLocalizacaoIbpmManager;
import br.com.infox.ibpm.manager.TarefaJbpmManager;
import br.com.infox.ibpm.manager.TarefaManager;
import br.com.itx.exception.AplicationException;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;

@Name(JbpmEventsHandler.NAME)
@Install(precedence=Install.FRAMEWORK)
@Scope(ScopeType.EVENT)
public class JbpmEventsHandler implements Serializable {

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
		throw new AplicationException(AplicationException.createMessage(
		        action + e.getLocalizedMessage(),
		        "removerProcessoLocalizacao()", "JbpmEventsHandler", "BPM"));
	}

	@Observer(Event.EVENTTYPE_TASK_END)
	@End(beforeRedirect=true)
    public void refreshPainel(ExecutionContext context) {
        context.getTaskInstance().setActorId(null);
        try {
            getProcessoManager().apagarActorIdDoProcesso(JbpmUtil.getProcesso());
        } catch (IllegalStateException e) {
            String action = "Limpar as variáveis do painel para atualização: ";
            LOG.error(action, e);
            throw new AplicationException(AplicationException.createMessage(
                    action + e.getLocalizedMessage(), "refreshPainel()",
                    "JbpmEventsHandler", "BPM"));
        } catch (IllegalArgumentException e) {
            String action = "Limpar as variáveis do painel para atualização: ";
            LOG.error(action, e);
            throw new AplicationException(AplicationException.createMessage(
                    action + e.getLocalizedMessage(), "refreshPainel()",
                    "JbpmEventsHandler", "BPM"));
        } catch (TransactionRequiredException e) {
            String action = "Limpar as variáveis do painel para atualização: ";
            LOG.error(action, e);
            throw new AplicationException(AplicationException.createMessage(
                    action + e.getLocalizedMessage(), "refreshPainel()",
                    "JbpmEventsHandler", "BPM"));
        }
    }
	
	/**
	 * Atualiza o dicionário de Tarefas (tb_tarefa) com seus respectivos id's 
	 * de todas as versões.
	 **/
	@Observer(ProcessBuilder.POST_DEPLOY_EVENT)
    public static void updatePostDeploy() {
        try {
            atualizarProcessos();
            getTarefaManager().encontrarNovasTarefas();
            getTarefaJbpmManager().inserirVersoesTarefas();
        } catch (IllegalStateException e) {
            String action = "Realizar atualização automáticas após publicação do fluxo: ";
            LOG.error(action, e);
            throw new AplicationException(AplicationException.createMessage(
                    action + e.getLocalizedMessage(), "updatePostDeploy()",
                    "JbpmEventsHandler", "BPM"));
        } catch (TransactionRequiredException e) {
            String action = "Realizar atualização automáticas após publicação do fluxo: ";
            LOG.error(action, e);
            throw new AplicationException(AplicationException.createMessage(
                    action + e.getLocalizedMessage(), "updatePostDeploy()",
                    "JbpmEventsHandler", "BPM"));
        }
    }
	
	private static void atualizarProcessos() {
		String sql = "update jbpm_processinstance pi set processdefinition_ = " +
				"(select max(id_) from jbpm_processdefinition pd " +
				"where name_ = (select name_ from jbpm_processdefinition " +
				"where id_ = pi.processdefinition_));\n" +

				"update jbpm_token t set node_ = "+
				"(select max(n.id_) from jbpm_node n "+
				"inner join jbpm_processdefinition pd on pd.id_ = n.processdefinition_ "+
				"where n.name_ = (select name_ from jbpm_node where id_ = t.node_) "+
				"and pd.name_ = (select procdef.name_ from jbpm_processinstance procinst "+
				"inner join jbpm_processdefinition procdef on procdef.id_ = procinst.processdefinition_ "+
				"where procinst.id_ = t.processinstance_) "+
				"and n.class_ = (select class_ from jbpm_node where id_ = t.node_));\n" +
				
				"update jbpm_taskinstance ti set task_ = "+ 
				"(select max(t.id_) from jbpm_task t "+
				"inner join jbpm_processdefinition pd on pd.id_ = t.processdefinition_ "+
				"where t.name_ = (select name_ from jbpm_task where id_ = ti.task_) and "+
				"pd.name_ = (select procdef.name_ from jbpm_processinstance procinst "+
				"inner join jbpm_processdefinition procdef on procdef.id_ = procinst.processdefinition_ "+
				"where procinst.id_ = ti.procinst_)) "+
				"where end_ is null;\n" +
				
				"update public.tb_processo_localizacao_ibpm pl set id_task_jbpm =" +
				"(select max(id_) from jbpm_task t where exists" +
				"(select * from jbpm_task where name_ = t.name_ " +
				"and id_ = pl.id_task_jbpm))";
		
		JbpmUtil.getJbpmSession().createSQLQuery(sql).executeUpdate();
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
		throw new AplicationException(AplicationException.createMessage(
		        action + e.getLocalizedMessage(), "removeCaixaProcesso()",
		        "JbpmEventsHandler", "BPM"));
	}
	
	private EntityManager getEntityManager() {
		return EntityUtil.getEntityManager();
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