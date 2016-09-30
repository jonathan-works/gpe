package br.com.infox.epp.quartz.ws.impl;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.contexts.Lifecycle;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.joda.time.DateTime;

import br.com.infox.cdi.producer.JbpmContextProducer;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.access.entity.BloqueioUsuario;
import br.com.infox.epp.access.manager.BloqueioUsuarioManager;
import br.com.infox.epp.calendario.CalendarioEventosService;
import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.timer.TaskExpiration;
import br.com.infox.epp.processo.timer.manager.TaskExpirationManager;
import br.com.infox.epp.quartz.ws.BamResource;
import br.com.infox.epp.quartz.ws.QuartzResource;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@RequestScoped
public class QuartzResourceImpl implements QuartzResource {
    
    private static final LogProvider LOG = Logging.getLogProvider(QuartzRestImpl.class);
    
    @Inject
    private BloqueioUsuarioManager bloqueioUsuarioManager;
    @Inject
    private ProcessoTarefaManager processoTarefaManager;
    @Inject
    private TaskExpirationManager taskExpirationManager;
    @Inject
    private PrazoComunicacaoService prazoComunicacaoService;
    @Inject
    private ProcessoManager processoManager;
    @Inject
    private CalendarioEventosService calendarioEventosService;
    @Inject
    private BamResourceImpl bamResourceImpl;
    @Inject
    private ApplicationServerService applicationServerService;

    @Override
    @Transactional
    public void processBloqueioUsuario() {
        List<BloqueioUsuario> bloqueios = bloqueioUsuarioManager.getBloqueiosAtivos();
        for (BloqueioUsuario bloqueio : bloqueios) {
            Date hoje = new Date();
            Date dataDesbloqueio = bloqueio.getDataPrevisaoDesbloqueio();
            if (dataDesbloqueio.before(hoje)) {
                try {
                    bloqueioUsuarioManager.desfazerBloqueioUsuario(bloqueio.getUsuario());
                } catch (DAOException e) {
                    LOG.error("quartzRestImpl.processBloqueioUsuario()", e);
                }
            }
        }
    }
    
    @Override
	public void taskExpirationProcessor() {
		Lifecycle.beginCall();
		try {
			List<ProcessoTarefa> processoTarefaList = processoTarefaManager.getWithTaskExpiration();
			for (ProcessoTarefa processoTarefa : processoTarefaList) {
			    Fluxo fluxo = processoTarefa.getProcesso().getNaturezaCategoriaFluxo().getFluxo();
			    String nomeTarefa = processoTarefa.getTarefa().getTarefa();
				TaskExpiration taskExpiration = taskExpirationManager.getByFluxoAndTaskName(fluxo, nomeTarefa);
				if (taskExpiration != null) {
					DateTime expirationDate = new DateTime(DateUtil.getEndOfDay(taskExpiration.getExpiration()));
					if (expirationDate.isBeforeNow()) {
					    TransactionManager transactionManager = applicationServerService.getTransactionManager();
						try {
						    transactionManager.begin();
						    TaskInstance taskInstance = ManagedJbpmContext.instance().getTaskInstanceForUpdate(processoTarefa.getTaskInstance());
							processoTarefaManager.finalizarInstanciaTarefa(taskInstance, taskExpiration.getTransition());
							transactionManager.commit();
						} catch (Exception e) {
							LOG.error("quartzRestImpl.processTaskExpiration()", e);
							try {
                                transactionManager.rollback();
                            } catch (IllegalStateException | SecurityException | SystemException e1) {
                                LOG.error("Error rolling back transaction", e1);
                            }
						}
					}
				}
			}
		} finally {
			Lifecycle.endCall();
		}
	}

    @Override
    @Transactional
    public BamResource getBamResource() {
        return bamResourceImpl;
    }
    
    @Override
    @Transactional
    public void retryAutomaticNodes() {
        Lifecycle.beginCall();
        JbpmContextProducer.createJbpmContextTransactional();
        JbpmContext jbpmContext = ComponentUtil.getComponent("org.jboss.seam.bpm.jbpmContext");
        try {
            List<Token> tokens = JbpmUtil.getTokensOfAutomaticNodesNotEnded();
            for (Token token : tokens) {
                Token tokenForUpdate = jbpmContext.getTokenForUpdate(token.getId());
                Node node = (Node) HibernateUtil.removeProxy(tokenForUpdate.getNode());
                ExecutionContext executionContext = new ExecutionContext(tokenForUpdate);
                TransactionManager transactionManager = applicationServerService.getTransactionManager();
                try {
                    transactionManager.begin();
                    node.execute(executionContext);
                    transactionManager.commit();
                } catch (Exception e) {
                    LOG.error("quartzRestImpl.processTaskExpiration()", e);
                    try {
                        transactionManager.rollback();
                    } catch (IllegalStateException | SecurityException | SystemException e1) {
                        LOG.error("Error rolling back transaction", e1);
                    }
                }
            } 
        } finally {
            Lifecycle.endCall();
        }
    }

    @Override
    @Transactional(timeout = 30000)
    public void processContagemPrazoComunicacao() {
        Lifecycle.beginCall();
        JbpmContextProducer.createJbpmContextTransactional();
        try {
            analisarProcessosAguardandoCiencia();
            analisarProcessosAguardandoCumprimento();
        } finally {
            Lifecycle.endCall();
        }
    }
    
    @Override
    @Transactional(timeout = 30000)
    public void processUpdateCalendarioSync() {
        Lifecycle.beginCall();
        try {
            calendarioEventosService.atualizarSeries();
            calendarioEventosService.removeOrphanSeries();
        } finally {
            Lifecycle.endCall();
        }
    }
    
    private void analisarProcessosAguardandoCumprimento() throws DAOException {
        List<Processo> processos = processoManager.listProcessosComunicacaoAguardandoCumprimento();
        for (Processo processo : processos) {
            if (!prazoComunicacaoService.hasPedidoProrrogacaoEmAberto(processo)){
                prazoComunicacaoService.movimentarComunicacaoPrazoExpirado(processo, ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO);
            }
        }
    }
    
    private void analisarProcessosAguardandoCiencia() throws DAOException {
        List<Processo> processos = processoManager.listProcessosComunicacaoAguardandoCiencia();
        for (Processo processo : processos) {
            prazoComunicacaoService.movimentarComunicacaoPrazoExpirado(processo, ComunicacaoMetadadoProvider.LIMITE_DATA_CIENCIA);
        }
    }

}
