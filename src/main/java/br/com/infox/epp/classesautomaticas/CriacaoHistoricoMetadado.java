package br.com.infox.epp.classesautomaticas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.TransactionManager;

import org.jboss.seam.contexts.Lifecycle;
import org.joda.time.DateTime;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.epp.processo.metadado.auditoria.HistoricoMetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.system.log.LogEventListener;
import br.com.infox.log.Logging;

public class CriacaoHistoricoMetadado implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "criacaoHistoricoMetadado";
    public final String DESCRICAO_ACAO = "Carga inicial";

    private EntityManager entityManager;
    private ControleClassesAutomaticas controle;
    private TransactionManager transactionManager = ApplicationServerService.instance().getTransactionManager();

    private CriacaoHistoricoMetadado() {
    }

    public static CriacaoHistoricoMetadado instance() {
        return new CriacaoHistoricoMetadado();
    }

    public void init() throws Exception {
        entityManager = EntityManagerProducer.instance().getEntityManagerNotManaged();
        try {
            Lifecycle.beginCall();
            LogEventListener.disableLogForEvent();
            if (isExecucaoValida()) {
                executar();
                updateExecucao();
            }
        } catch (Exception e) {
            Logging.getLogProvider(CriacaoHistoricoMetadado.class).error("", e);
            excluirDadosInseridosParcialmente();
            throw e;
        } finally {
            LogEventListener.enableLog();
            Lifecycle.endCall();
        }

    }

    private void excluirDadosInseridosParcialmente() {
        entityManager = EntityManagerProducer.instance().getEntityManagerNotManaged();
        String sql = "delete from tb_historico_metadado_processo where ds_acao = '" + DESCRICAO_ACAO + "'";
        entityManager.createNativeQuery(sql).executeUpdate();

    }
    
    private boolean isExecucaoValida() {
        try {
            controle = getControle();
            return controle.isExecutar();
        } catch (NoResultException e) {
            controle = new ControleClassesAutomaticas();
            controle.setNomeClasse(NAME);
            controle.setExecutar(true);
            entityManager.persist(controle);
            return true;
        }
    }

    private ControleClassesAutomaticas getControle() {
        return entityManager
                .createQuery("select cca from ControleClassesAutomaticas cca where cca.nomeClasse = ?", ControleClassesAutomaticas.class)
                .setParameter(1, NAME).getSingleResult();
    }

    private void updateExecucao() {
        controle.setExecutar(false);
        entityManager.merge(controle);
    }

    private void executar() throws Exception {
        int contador = 0;
        List<MetadadoProcesso> lista = getMedados();
        beginTransaction();
        for (MetadadoProcesso mp : lista) {
            HistoricoMetadadoProcesso log = new HistoricoMetadadoProcesso();
            log.setIdMetadadoProcesso(mp.getId());
            log.setNome(mp.getMetadadoType());
            log.setValor(mp.getValor());
            log.setValorObjeto(mp.toString());
            log.setClassType(mp.getClassType());
            log.setIdProcesso(mp.getProcesso().getIdProcesso().longValue());
            log.setDataRegistro(DateTime.now().toDate());
            log.setAcao(DESCRICAO_ACAO);
            entityManager.persist(log);

            if (contador >= 5000) {
                commitTransaction();
                beginTransaction();
                contador = 0;
            }
            contador++;
        }
        commitTransaction();
    }
    
    private void beginTransaction() throws Exception {
        transactionManager.begin();
        transactionManager.setTransactionTimeout(3600);
    }
    
    private void commitTransaction() throws Exception {
        entityManager.flush();
        transactionManager.commit();
    }

    private List<MetadadoProcesso> getMedados() {
        List<MetadadoProcesso> resultList = entityManager.createQuery("select m from MetadadoProcesso m", MetadadoProcesso.class)
                .getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return new ArrayList<MetadadoProcesso>();
    }

}
