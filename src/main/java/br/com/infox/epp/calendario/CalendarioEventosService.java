package br.com.infox.epp.calendario;

import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_BY_SERIE;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_ORPHAN_SERIES;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.GET_PERIODICOS_NAO_ATUALIZADOS;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.DATA;
import static br.com.infox.epp.cliente.query.CalendarioEventosQuery.Param.SERIE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.calendario.entity.SerieEventos;
import br.com.infox.epp.calendario.modification.process.CalendarioEventosModificationProcessor;
import br.com.infox.epp.cliente.entity.CalendarioEventos;

@Stateless
public class CalendarioEventosService extends PersistenceController {

    @Inject
    private CalendarioEventosModificationProcessor calendarioEventosProcessor;

    public void atualizarSeries() {
        for (CalendarioEventos calendarioEventos : getNotUpToDate(getDataLimite())) {
            List<CalendarioEventosModification> modifications = criarSerie(calendarioEventos);
            if (!CalendarioEventosModification.hasIssues(modifications)){
                persistir(modifications);
            }
        }
    }

    public List<CalendarioEventos> getBySerie(SerieEventos serie) {
        EntityManager em = getEntityManager();
        TypedQuery<CalendarioEventos> query = em.createNamedQuery(GET_BY_SERIE, CalendarioEventos.class);
        query = query.setParameter(SERIE, serie);
        return query.getResultList();
    }

    private java.util.Date getDataLimite() {
        return DateUtil.getBeginningOfDay(DateTime.now().plusYears(1)).toDate();
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

    // CRUD

    public List<CalendarioEventosModification> criar(CalendarioEventos calendarioEventos) {
        List<CalendarioEventosModification> result = new ArrayList<>();
        result.add(new CalendarioEventosModification(calendarioEventos));
        result.addAll(criarSerie(calendarioEventos));
        return result;
    }

    private List<CalendarioEventosModification> criarSerie(CalendarioEventos calendarioEventos) {
        Date dataLimite = getDataLimite();
        List<CalendarioEventosModification> result = new ArrayList<>();
        CalendarioEventos proximoEvento = calendarioEventos;
        if (proximoEvento != null && proximoEvento.getSerie() != null) {
            while (proximoEvento != null && proximoEvento.isBefore(dataLimite)) {
                proximoEvento = proximoEvento.getProximoEvento();
                result.add(new CalendarioEventosModification(proximoEvento));
            }
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void persistir(List<CalendarioEventosModification> modifications) {
        try {
            for (CalendarioEventosModification modification : modifications) {
                if (modification.getEvento().getSerie() != null && modification.getEvento().getSerie().getId() == null) {
                    getEntityManager().persist(modification.getEvento().getSerie());
                }
                getEntityManager().persist(modification.getEvento());
                getEntityManager().flush();
                calendarioEventosProcessor.afterPersist(modification);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remover(CalendarioEventos eventoRemover, boolean removeSerie) {
        List<CalendarioEventos> result = new ArrayList<>();
        SerieEventos serie = eventoRemover.getSerie();
        if (removeSerie && serie != null) {
            for (CalendarioEventos evento : getBySerie(eventoRemover.getSerie())) {
                result.add(evento);
            }
        } else {
            result.add(eventoRemover);
        }
        try {
            for (CalendarioEventos calendarioEventos : result) {
                getEntityManager().remove(calendarioEventos);
            }
            if (removeSerie) {
                getEntityManager().remove(serie);
            }
            getEntityManager().flush();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void atualizar(CalendarioEventos eventoAtualizar) {
        try {
            if (eventoAtualizar != null) {
                if (eventoAtualizar.getSerie() != null) {
                    for (CalendarioEventos evento : getBySerie(eventoAtualizar.getSerie())) {
                        evento.setDescricaoEvento(eventoAtualizar.getDescricaoEvento());
                        getEntityManager().merge(evento);
                    }
                } else {
                    getEntityManager().merge(eventoAtualizar);
                }
            }
            getEntityManager().flush();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

}
