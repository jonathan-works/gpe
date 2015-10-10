package br.com.infox.epp.system.log;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.time.StopWatch;
import org.hibernate.persister.entity.EntityPersister;
import org.jboss.seam.contexts.Contexts;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.util.ArrayUtil;
import br.com.infox.epp.system.entity.EntityLog;
import br.com.infox.epp.system.entity.EntityLogDetail;
import br.com.infox.epp.system.type.TipoOperacaoLogEnum;
import br.com.infox.epp.system.util.LogUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public class ExecuteLog {

    private static final LogProvider LOG = Logging.getLogProvider(ExecuteLog.class);
    private Object[] oldState;
    private Object[] state;
    private EntityPersister persister;
    private Object entidade;
    private TipoOperacaoLogEnum tipoOperacao;
    private EntityManager em;
    private StopWatch sw;

    public ExecuteLog() {
        sw = new StopWatch();
        sw.start();
        em = EntityManagerProducer.getEntityManagerLog();
    }

    public Object[] getOldState() {
        return ArrayUtil.copyOf(oldState);
    }

    public void setOldState(Object[] oldState) {
        this.oldState = ArrayUtil.copyOf(oldState);
    }

    public void setState(Object[] state) {
        this.state = ArrayUtil.copyOf(state);
    }

    public EntityPersister getPersister() {
        return persister;
    }

    public void setPersister(EntityPersister persister) {
        this.persister = persister;
    }

    public Object getEntidade() {
        return entidade;
    }

    public void setEntidade(Object entidade) {
        this.entidade = entidade;
    }

    public TipoOperacaoLogEnum getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacaoLogEnum tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    private void init() {
        if (tipoOperacao.equals(TipoOperacaoLogEnum.I)) {
            oldState = new Object[state.length];
        } else if (tipoOperacao.equals(TipoOperacaoLogEnum.D)) {
            state = new Object[oldState.length];
        }
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public void execute() {
        if (!Contexts.isSessionContextActive()) {
            return;
        }

        init();

        String[] nomes = persister.getClassMetadata().getPropertyNames();
        EntityLog logEnt = LogUtil.createEntityLog(entidade);
        logEnt.setTipoOperacao(tipoOperacao);

        em.persist(logEnt);

        for (int i = 0; i < nomes.length; i++) {
            try {
                if (!LogUtil.isCollection(entidade, nomes[i])
                        && !LogUtil.isBinario(entidade, nomes[i])
                        && !LogUtil.compareObj(oldState[i], state[i])) {
                    EntityLogDetail detail = new EntityLogDetail();
                    detail.setEntityLog(logEnt);
                    detail.setNomeAtributo(nomes[i]);
                    detail.setValorAtual(LogUtil.toStringForLog(state[i]));
                    detail.setValorAnterior(LogUtil.toStringForLog(oldState[i]));
                    em.persist(detail);
                    logEnt.getLogDetalheList().add(detail);
                }
            } catch (Exception e) {
                LOG.info("Erro ao logar", e);
                LOG.error(".execute()", e);
            }
        }
        em.flush();

        StringBuilder sb = new StringBuilder();
        sb.append(".execute(): ").append(tipoOperacao.getLabel());
        sb.append(" (").append(entidade.getClass().getName()).append("): ");
        sb.append(sw.getTime());
        LOG.info(sb.toString());
    }

}
