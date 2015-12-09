package br.com.infox.epp.calendario;

import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_BY_SERIE;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_BY_SERIE_AFTER_DATE;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_ORPHAN_SERIES;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_PERIODICOS_NAO_ATUALIZADOS;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.DATA;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.DATA_FIM;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.SERIE;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.calendario.entity.SerieEventos;
import br.com.infox.epp.calendario.modification.process.CalendarioEventosModificationProcessor;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.cliente.dao.CalendarioEventosDAO;
import br.com.infox.epp.cliente.entity.CalendarioEventos;
import br.com.infox.epp.cliente.manager.CalendarioEventosManager;
import br.com.infox.util.time.Date;

@Stateless
public class CalendarioEventosService {

    @Inject
    private CalendarioEventosModificationProcessor calendarioEventosProcessor;
    @Inject
    private CalendarioEventosDAO calendarioEventosDao;
    @Inject
    private CalendarioEventosManager calendarioEventosManager;

    public List<CalendarioEventosModification> atualizar(CalendarioEventos calendarioEventos) {
        CalendarioEventosModification modification = new CalendarioEventosModification(null, calendarioEventos);
        getEntityManager().refresh(calendarioEventos);
        modification.setBefore(calendarioEventos);
        final List<CalendarioEventosModification> result = new ArrayList<>();
        result.add(modification);
        result.addAll(removerSerie(modification.getBefore()));
        result.addAll(criarSerie(modification.getAfter()));
        return result;
    }

    private void atualizar(CalendarioEventosModification modification) {
        try {
            if (modification.getAfter().getSerie() != null && modification.getAfter().getSerie().getId() ==null){
                getEntityManager().persist(modification.getAfter().getSerie());
            }
            getEntityManager().merge(modification.getAfter());
            getEntityManager().flush();
            calendarioEventosProcessor.afterPersist(modification);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    public void atualizarSeries() {
        for (CalendarioEventos calendarioEventos : getNotUpToDate(getDataLimite())) {
            List<CalendarioEventosModification> modifications = criarSerie(calendarioEventos);
            if (!CalendarioEventosModification.hasIssues(modifications)){
                persistir(modifications);
            }
        }
    }

    public List<CalendarioEventosModification> criar(CalendarioEventos calendarioEventos) {
        final List<CalendarioEventosModification> result = new ArrayList<>();
        result.add(new CalendarioEventosModification(null, calendarioEventos));
        result.addAll(criarSerie(calendarioEventos));
        return result;
    }

    private void criar(CalendarioEventosModification modification) {
        try {
            if (modification.getAfter().getSerie() != null && modification.getAfter().getSerie().getId() ==null){
                getEntityManager().persist(modification.getAfter().getSerie());
            }
            getEntityManager().persist(modification.getAfter());
            getEntityManager().flush();
            calendarioEventosProcessor.afterPersist(modification);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    private List<CalendarioEventosModification> criarSerie(CalendarioEventos calendarioEventos) {
        java.util.Date dataLimite = getDataLimite();
        final List<CalendarioEventosModification> result = new ArrayList<>();
        if (calendarioEventos != null && calendarioEventos.getSerie() != null) {
            CalendarioEventos proximoEvento = calendarioEventos;
            while ((proximoEvento = proximoEvento.getProximoEvento()) != null && proximoEvento.isBefore(dataLimite)) {
                result.add(new CalendarioEventosModification(null, proximoEvento));
            }
        }
        return result;
    }

    public List<CalendarioEventos> getBySerie(SerieEventos serie) {
        EntityManager em = getEntityManager();
        TypedQuery<CalendarioEventos> query = em.createNamedQuery(GET_BY_SERIE, CalendarioEventos.class);
        query = query.setParameter(SERIE, serie);
        return query.getResultList();
    }

    private List<CalendarioEventos> getBySerieAfterDate(SerieEventos serie, java.util.Date dataFim) {
        EntityManager em = getEntityManager();
        TypedQuery<CalendarioEventos> query = em.createNamedQuery(GET_BY_SERIE_AFTER_DATE, CalendarioEventos.class);
        query = query.setParameter(SERIE, serie).setParameter(DATA_FIM, dataFim);
        return query.getResultList();
    }

    private java.util.Date getDataLimite() {
        return new Date().plusYears(1).withTimeAtStartOfDay();
    }

    private EntityManager getEntityManager() {
        return BeanManager.INSTANCE.getReference(EntityManager.class);
    }

    private List<CalendarioEventos> getNotUpToDate(java.util.Date data) {
        EntityManager em = getEntityManager();
        TypedQuery<CalendarioEventos> query = em.createNamedQuery(GET_PERIODICOS_NAO_ATUALIZADOS,
                CalendarioEventos.class);
        query = query.setParameter(DATA, data);
        return query.getResultList();
    }

    private List<SerieEventos> getSeriesOrfas() {
        EntityManager em = getEntityManager();
        TypedQuery<SerieEventos> query = em.createNamedQuery(GET_ORPHAN_SERIES, SerieEventos.class);
        return query.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persistir(CalendarioEventosModification modification) {
        switch (modification.getType()) {
        case CREATE:
            criar(modification);
            break;
        case UPDATE:
            atualizar(modification);
            break;
        case DELETE:
            remover(modification);
            break;
        default:
            break;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persistir(List<CalendarioEventosModification> modifications) {
        for (CalendarioEventosModification modification : modifications) {
            persistir(modification);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeOrphanSeries() {
        try {
            EntityManager entityManager = getEntityManager();
            for (SerieEventos serieEventos : getSeriesOrfas()) {
                entityManager.remove(serieEventos);
            }
            entityManager.flush();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    public List<CalendarioEventosModification> remover(CalendarioEventos calendarioEventos) {
        final List<CalendarioEventosModification> result = new ArrayList<>();
        result.add(new CalendarioEventosModification(calendarioEventos, null));
        result.addAll(removerSerie(calendarioEventos));
        return result;
    }

    private void remover(CalendarioEventosModification modification) {
        try {
            CalendarioEventos calendarioEventos = modification.getBefore();
            getEntityManager().remove(getEntityManager().merge(calendarioEventos));
            getEntityManager().flush();
            calendarioEventosProcessor.afterPersist(modification);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    private List<CalendarioEventosModification> removerSerie(CalendarioEventos calendarioEventos) {
        final List<CalendarioEventosModification> result = new ArrayList<>();
        if (calendarioEventos != null && calendarioEventos.getSerie() != null) {
            /**
             * TODO: confirmar se é pra remover apenas eventos futuros, ou todos os eventos da série
             */
            for (CalendarioEventos evento : getBySerieAfterDate(calendarioEventos.getSerie(), new Date().withTimeAtStartOfDay())) {
                if (!calendarioEventos.equals(evento)) {
                    result.add(new CalendarioEventosModification(evento, null));
                }
            }
        }
        return result;
    }
    
}
