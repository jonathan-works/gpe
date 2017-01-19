package br.com.infox.epp.quartz.ws.impl;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.jboss.seam.contexts.Lifecycle;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import br.com.infox.cdi.producer.JbpmContextProducer;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.epp.access.entity.BloqueioUsuario;
import br.com.infox.epp.access.manager.BloqueioUsuarioManager;
import br.com.infox.epp.calendario.CalendarioEventosService;
import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.quartz.ws.BamResource;
import br.com.infox.epp.quartz.ws.QuartzResource;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.ibpm.util.JbpmUtil;

@RequestScoped
public class QuartzResourceImpl implements QuartzResource {
    
    private static final Logger LOG = Logger.getLogger(QuartzRestImpl.class.getName());
    
    @Inject
    private BloqueioUsuarioManager bloqueioUsuarioManager;
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
                    LOG.log(Level.SEVERE, "quartzRestImpl.processBloqueioUsuario()", e);
                }
            }
        }
    }
    
    @Override
    @Transactional
    public BamResource getBamResource() {
        return bamResourceImpl;
    }
    
    @Override
    public void retryAutomaticNodes() {
        Lifecycle.beginCall();
        try {
            List<Token> tokens = JbpmUtil.getTokensOfAutomaticNodesNotEnded();
            for (Token token : tokens) {
                TransactionManager transactionManager = applicationServerService.getTransactionManager();
                try {
                    transactionManager.begin();
                    JbpmContext jbpmContext = JbpmContextProducer.createJbpmContextTransactional();
                    Token tokenForUpdate = jbpmContext.getTokenForUpdate(token.getId());
                    Node node = (Node) HibernateUtil.removeProxy(tokenForUpdate.getNode());
                    ExecutionContext executionContext = new ExecutionContext(tokenForUpdate);
                    node.execute(executionContext);
                    transactionManager.commit();
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "quartzRestImpl.processTaskExpiration()", e);
                    try {
                        transactionManager.rollback();
                    } catch (IllegalStateException | SecurityException | SystemException e1) {
                        LOG.log(Level.SEVERE, "Error rolling back transaction", e1);
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
