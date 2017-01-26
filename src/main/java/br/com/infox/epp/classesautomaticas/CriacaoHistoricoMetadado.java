package br.com.infox.epp.classesautomaticas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.TransactionManager;

import org.jboss.seam.contexts.Lifecycle;
import org.joda.time.DateTime;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.processo.metadado.auditoria.HistoricoMetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.system.Configuration;

public class CriacaoHistoricoMetadado implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public static final String NAME = "criacaoHistoricoMetadado";
    public final String DESCRICAO_ACAO = "Carga inicial";

    private static Logger log = Logger.getLogger(CriacaoHistoricoMetadado.class.getName());
    private EntityManager entityManager;
    private ControleClassesAutomaticas controle;
    private TransactionManager transactionManager;

    private CriacaoHistoricoMetadado() {
    }

    public static CriacaoHistoricoMetadado instance() {
        return new CriacaoHistoricoMetadado();
    }

    public void init() throws Exception {
        transactionManager = Configuration.getInstance().getApplicationServer().getTransactionManager();
        Lifecycle.beginCall();
        try {
            transactionManager.setTransactionTimeout(3600);
            transactionManager.begin();
            entityManager = EntityManagerProducer.instance().getEntityManagerTransactional();
            if (isExecucaoValida()) {
                executar();
                updateExecucao();
            }
            transactionManager.commit();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Erro ao excluir carga inicial do histórico dos metadados", e);
            transactionManager.rollback();
            throw e;
        } finally {
            Lifecycle.endCall();
        }
    }

    private boolean isExecucaoValida() {
     // TODO corrigir para integrar epp 2.14
//        try {
//            controle = getControle();
//            return controle.isExecutar();
//        } catch (NoResultException e) {
//            controle = new ControleClassesAutomaticas();
//            controle.setNomeClasse(NAME);
//            controle.setExecutar(true);
//            entityManager.persist(controle);
//            entityManager.flush();
//            return true;
//        }
        return false;
    }

    private ControleClassesAutomaticas getControle() {
        return entityManager.createQuery("select cca from ControleClassesAutomaticas cca where cca.nomeClasse = ?", ControleClassesAutomaticas.class)
                .setParameter(1, NAME).getSingleResult();
    }

    private void updateExecucao() throws Exception {
        controle.setExecutar(false);
        controle = entityManager.merge(controle);
        entityManager.flush();
    }

    private void executar() throws Exception {
        int contador = 0;
        Date hoje = DateTime.now().toDate();
        List<MetadadoProcesso> lista = getMetadados();
        for (MetadadoProcesso mp : lista) {
            HistoricoMetadadoProcesso log = new HistoricoMetadadoProcesso();
            log.setIdMetadadoProcesso(mp.getId());
            log.setNome(mp.getMetadadoType());
            log.setValor(mp.getValor());
            log.setDescricao(mp.toString());
            log.setClassType(mp.getClassType());
            log.setIdProcesso(mp.getProcesso().getIdProcesso().longValue());
            log.setDataRegistro(hoje);
            log.setAcao(DESCRICAO_ACAO);
            entityManager.persist(log);

            if (contador >= 5000) {
                entityManager.flush();
                entityManager.clear();
                contador = 0;
            }
            contador++;
        }
    }

    private List<MetadadoProcesso> getMetadados() {
        List<MetadadoProcesso> resultList = entityManager.createQuery("select m from MetadadoProcesso m inner join fetch m.processo ", MetadadoProcesso.class).getResultList();
        try {
            if (resultList != null && !resultList.isEmpty()) {
                return resultList;
            }
            return new ArrayList<MetadadoProcesso>();
        } finally {
            entityManager.clear(); //limpa o entityManager para remover os dados da consulta do cache de 1º nível
        }
    }

}
