package br.com.infox.epp.quartz.ws;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.ws.rs.WebApplicationException;

import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.transaction.Transaction;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.joda.time.DateTime;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.report.RequestInternalPageService;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.access.entity.BloqueioUsuario;
import br.com.infox.epp.access.manager.BloqueioUsuarioManager;
import br.com.infox.epp.calendario.CalendarioEventosService;
import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.epp.estatistica.manager.BamTimerManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.timer.TaskExpiration;
import br.com.infox.epp.processo.timer.manager.TaskExpirationManager;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public class QuartzRestImpl implements QuartzRest {
    
    private static final LogProvider LOG = Logging.getLogProvider(QuartzRestImpl.class);
    
    @Inject
    private BloqueioUsuarioManager bloqueioUsuarioManager;
    @Inject
    private RequestInternalPageService requestInternalPageService;
    @Inject
    private ProcessoTarefaManager processoTarefaManager;
    @Inject
    private TaskExpirationManager taskExpirationManager;
    @Inject
    private BamTimerManager bamTimerManager;
    @Inject
    private PrazoComunicacaoService prazoComunicacaoService;
    @Inject
    private ProcessoManager processoManager;
    @Inject
    private CalendarioEventosService calendarioEventosService;

    @Override
    @Transactional
    public void processBloqueioUsuario(String key) {
        if (!requestInternalPageService.isValid(key)) throw new WebApplicationException(401);
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
    @Transactional
    public void taskExpirationProcessor(String key) {
        if (!requestInternalPageService.isValid(key)) throw new WebApplicationException(401);
        List<ProcessoTarefa> processoTarefaList = this.processoTarefaManager.getWithTaskExpiration();
        for (ProcessoTarefa processoTarefa : processoTarefaList) {
            TaskExpiration taskExpiration = this.taskExpirationManager.getByFluxoAndTaskName(processoTarefa.getProcesso().getNaturezaCategoriaFluxo().getFluxo(), processoTarefa.getTarefa().getTarefa());
            if (taskExpiration != null) {
                DateTime expirationDate = new DateTime(DateUtil.getEndOfDay(taskExpiration.getExpiration()));
                if (expirationDate.isBeforeNow()) {
                    BusinessProcess.instance().setProcessId(processoTarefa.getProcesso().getIdJbpm());
                    BusinessProcess.instance().setTaskId(processoTarefa.getTaskInstance());
                    TaskInstance taskInstance = org.jboss.seam.bpm.TaskInstance.instance();
                    try {
                        this.processoTarefaManager.finalizarInstanciaTarefa(taskInstance, taskExpiration.getTransition());
                    } catch (DAOException e) {
                        LOG.error("quartzRestImpl.processTaskExpiration()", e);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public BamResource getBamResource(String key) {
        if (!requestInternalPageService.isValid(key)) throw new WebApplicationException(401);
        return new BamResourceImpl(bamTimerManager, processoTarefaManager);
    }
    
    @Override
    @Transactional
    public void retryAutomaticNodes(String key) {
        if (!requestInternalPageService.isValid(key)) throw new WebApplicationException(401);
        Lifecycle.beginCall();
        try {
            List<Token> tokens = JbpmUtil.getTokensOfAutomaticNodesNotEnded();
            for (Token token : tokens) {
                Node node = (Node) HibernateUtil.removeProxy(token.getNode());
                ExecutionContext executionContext = new ExecutionContext(token);
                node.execute(executionContext);
            } 
        } finally {
            Lifecycle.endCall();
        }
    }

    @Override
    public void processContagemPrazoComunicacao(String key) {
        if (!requestInternalPageService.isValid(key)) throw new WebApplicationException(401);
        Lifecycle.beginCall();
        try {
            UserTransaction transaction = Transaction.instance();
            try {
                transaction.setTransactionTimeout(30000);
                transaction.begin();
                analisarProcessosAguardandoCiencia();
                analisarProcessosAguardandoCumprimento();
                transaction.commit();
            } catch (Exception e) {
                try {
                    transaction.rollback();
                } catch (IllegalStateException | SecurityException | SystemException e1) {
                    LOG.error(e);
                }
                LOG.error(e);
            } 
        } finally {
            Lifecycle.endCall();
        }
    }
    
    @Override
    public void processUpdateCalendarioSync(String key) {
        if (!requestInternalPageService.isValid(key)) throw new WebApplicationException(401);
        Lifecycle.beginCall();
        try {
            UserTransaction transaction = Transaction.instance();
            try {
                transaction.setTransactionTimeout(30000);
                transaction.begin();
                calendarioEventosService.atualizarSeries();
                calendarioEventosService.removeOrphanSeries();
                transaction.commit();
            } catch (Exception e) {
                try {
                    transaction.rollback();
                } catch (IllegalStateException | SecurityException | SystemException e1) {
                    throw new RuntimeException(e1);
                }
                LOG.error(e);
            }
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
