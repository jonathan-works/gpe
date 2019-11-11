package br.com.infox.epp.cliente.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.joda.time.DateTime;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.calendario.TipoEvento;
import br.com.infox.epp.cliente.entity.CalendarioEventos;
import br.com.infox.epp.cliente.entity.CalendarioEventos_;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.system.Parametros;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CalendarioEventosSearch extends PersistenceController {

    @Inject
    private FluxoDAO fluxoDAO;

    public Date getProximoFimPrazoConsiderandoSuspensaoPrazos(DateTime dataFimPrazo, CalendarioEventos novoEvento) {
        dataFimPrazo = DateUtil.getBeginningOfDay(dataFimPrazo);
        CalendarioEventos feriado = null;

        while (novoEvento != null) {
            if (novoEvento.isSuspensaoPrazo()) {
                // Adiciona a suspensão de prazo
                long fimPrazo = dataFimPrazo.getMillis();
                long inicioSuspensao = novoEvento.getDateTimeInicio().getMillis();
                long fimSuspensao = novoEvento.getDateTimeFim().getMillis();

                long diferenca = fimPrazo - inicioSuspensao;
                if (diferenca >= 0) {
                    fimPrazo = fimSuspensao + diferenca;
                    dataFimPrazo = new DateTime(fimPrazo).plusDays(1);
                }
                feriado = getFeriadoInfluenciaNaData(dataFimPrazo);
            } else {
                feriado = novoEvento;
            }

            // Impede que o prazo termine em final de semana ou feriado
            while (feriado != null || DateUtil.isFinalDeSemana(dataFimPrazo)) {
                if (DateUtil.isFinalDeSemana(dataFimPrazo)) {
                    dataFimPrazo = dataFimPrazo.plusDays(1);
                }
                if (feriado != null) {
                    dataFimPrazo = feriado.getDateTimeFim().plusDays(1);
                }
                feriado = getFeriadoInfluenciaNaData(dataFimPrazo);
            }
            novoEvento = getSuspensaoInfluenciaNaData(dataFimPrazo.toDate());
        }

        return DateUtil.getEndOfDay(dataFimPrazo).toDate();
    }

    // Feriado mais longo que iniciou antes ou exatamente no dia passado e
    // terminou depois ou exatamente no dia passado
    private CalendarioEventos getFeriadoInfluenciaNaData(DateTime dia) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CalendarioEventos> query = cb.createQuery(CalendarioEventos.class);
        Root<CalendarioEventos> feriado = query.from(CalendarioEventos.class);
        query.where(cb.equal(feriado.get(CalendarioEventos_.tipoEvento), TipoEvento.F),
                cb.lessThanOrEqualTo(feriado.get(CalendarioEventos_.dataInicio), dia.toDate()),
                cb.greaterThanOrEqualTo(feriado.get(CalendarioEventos_.dataFim), dia.toDate()));
        query.orderBy(cb.desc(feriado.get(CalendarioEventos_.dataFim)));
        List<CalendarioEventos> result = getEntityManager().createQuery(query).setMaxResults(1).getResultList();
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    // Suspensão mais longa que iniciou antes ou exatamente no dia passado e
    // terminou depois ou exatamente no dia passado
    private CalendarioEventos getSuspensaoInfluenciaNaData(Date data) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CalendarioEventos> query = cb.createQuery(CalendarioEventos.class);
        Root<CalendarioEventos> evento = query.from(CalendarioEventos.class);
        query.where(cb.equal(evento.get(CalendarioEventos_.tipoEvento), TipoEvento.S),
                cb.lessThanOrEqualTo(evento.get(CalendarioEventos_.dataInicio), data),
                cb.greaterThanOrEqualTo(evento.get(CalendarioEventos_.dataFim), data));
        query.orderBy(cb.asc(evento.get(CalendarioEventos_.dataInicio)));
        List<CalendarioEventos> result = getEntityManager().createQuery(query).setMaxResults(1).getResultList();
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    public boolean existeColisaoDeSuspensaoDePrazo(Date dataInicio, Date dataFim) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CalendarioEventos> evento = query.from(CalendarioEventos.class);
        query.where(cb.equal(evento.get(CalendarioEventos_.tipoEvento), TipoEvento.S),
                cb.or(cb.and(cb.lessThanOrEqualTo(evento.get(CalendarioEventos_.dataInicio), dataInicio),
                        cb.greaterThanOrEqualTo(evento.get(CalendarioEventos_.dataFim), dataInicio)),
                        cb.and(cb.lessThanOrEqualTo(evento.get(CalendarioEventos_.dataInicio), dataFim),
                                cb.greaterThanOrEqualTo(evento.get(CalendarioEventos_.dataFim), dataFim)),
                        cb.and(cb.lessThanOrEqualTo(evento.get(CalendarioEventos_.dataInicio), dataInicio),
                                cb.greaterThanOrEqualTo(evento.get(CalendarioEventos_.dataFim), dataFim))));
        query.select(cb.count(evento));
        return getEntityManager().createQuery(query).getSingleResult() > 0;
    }

    public CalendarioEventos find(Integer id) {
        return getEntityManager().find(CalendarioEventos.class, id);
    }

    public long getCountComunicacoesAfetadasByNovoEvento(CalendarioEventos novoEvento) {
        Date dataInicio = novoEvento.getDataInicio();
        Date dataFim = null;
        if (!novoEvento.isSuspensaoPrazo()) {
            dataFim = novoEvento.getDataFim();
        }
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<MetadadoProcesso> metadado = query.from(MetadadoProcesso.class);
        Join<MetadadoProcesso, Processo> processo = metadado.join(MetadadoProcesso_.processo);
        query.where(cb.or(
                createrPredicateComunicacoesAguardandoCiencia(cb, query, metadado, processo, dataInicio, dataFim),
                createPredicateComunicacoesAguardandoCumprimento(cb, query, metadado, processo, dataInicio, dataFim)));
        query.select(cb.count(processo));
        return getEntityManager().createQuery(query).getSingleResult();
    }

    public List<Processo> listComunicacoesAguardandoCienciaInfluenciadasPorSuspensaoPrazoAposData(Date data) {
        return createQueryListComunicacoesAguardandoCiencia(data, null);
    }

    public List<Processo> listComunicacoesAguardandoCienciaInfluenciadasPorFeriadoNoIntervalo(Date dataInicio, Date dataFim) {
        return createQueryListComunicacoesAguardandoCiencia(dataInicio, dataFim);
    }

    private List<Processo> createQueryListComunicacoesAguardandoCiencia(Date dataInicio, Date dataFim) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Processo> query = cb.createQuery(Processo.class);
        Root<MetadadoProcesso> metadadoCiencia = query.from(MetadadoProcesso.class);
        Join<MetadadoProcesso, Processo> processo = metadadoCiencia.join(MetadadoProcesso_.processo);
        query.where(createrPredicateComunicacoesAguardandoCiencia(cb, query, metadadoCiencia, processo, dataInicio, dataFim));
        query.select(processo);
        return getEntityManager().createQuery(query).getResultList();
    }

    protected Predicate createrPredicateComunicacoesAguardandoCiencia(CriteriaBuilder cb, CriteriaQuery<?> query,
            Root<MetadadoProcesso> metadadoCiencia, Join<MetadadoProcesso, Processo> processo, Date dataInicio, Date dataFim) {

        Subquery<Integer> subDataCiencia = createSubqueryMetadado(cb, query, processo,
                ComunicacaoMetadadoProvider.DATA_CIENCIA.getMetadadoType());
        Predicate where = cb.and(
                cb.equal(metadadoCiencia.get(MetadadoProcesso_.metadadoType),
                        ComunicacaoMetadadoProvider.LIMITE_DATA_CIENCIA.getMetadadoType()),
                cb.greaterThanOrEqualTo(
                        cb.function("to_date", Date.class, metadadoCiencia.get(MetadadoProcesso_.valor)), dataInicio),
                cb.isNotNull(processo.get(Processo_.idJbpm)), cb.isNull(processo.get(Processo_.dataFim)),
                cb.exists(subDataCiencia).not(), cb.exists(createSubqueryTipoProcessoComunicacao(cb, query, processo)),
                cb.exists(createSubqueryFluxosComunicacao(cb, query, processo)));
        if (dataFim != null) {
            where = cb.and(where, cb.lessThanOrEqualTo(
                    cb.function("to_date", Date.class, metadadoCiencia.get(MetadadoProcesso_.valor)), dataFim));
        }
        return where;
    }

    public List<Processo> listComunicacoesAguardandoCumprimentoInfluenciadasPorSuspensaoPrazoAposData(Date data) {
        return createQueryListComunicacoesAguardadoCumprimento(data, null);
    }

    public List<Processo> listComunicacoesAguardandoCumprimentoInfluenciadasPorFeriadoNoIntervalo(Date dataInicio, Date dataFim) {
        return createQueryListComunicacoesAguardadoCumprimento(dataInicio, dataFim);
    }

    private List<Processo> createQueryListComunicacoesAguardadoCumprimento(Date dataInicio, Date dataFim) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Processo> query = cb.createQuery(Processo.class);
        Root<MetadadoProcesso> metadadoCumprimento = query.from(MetadadoProcesso.class);
        Join<MetadadoProcesso, Processo> processo = metadadoCumprimento.join(MetadadoProcesso_.processo);
        query.where(createPredicateComunicacoesAguardandoCumprimento(cb, query, metadadoCumprimento, processo, dataInicio, dataFim));
        query.select(processo);
        return getEntityManager().createQuery(query).getResultList();
    }

    protected Predicate createPredicateComunicacoesAguardandoCumprimento(CriteriaBuilder cb, CriteriaQuery<?> query,
            Root<MetadadoProcesso> metadadoCumprimento, Join<MetadadoProcesso, Processo> processo, Date dataInicio, Date dataFim) {

        Subquery<Integer> subComunicacao = createSubqueryTipoProcessoComunicacao(cb, query, processo);
        Subquery<Integer> subDataCiencia = createSubqueryMetadado(cb, query, processo,
                ComunicacaoMetadadoProvider.DATA_CIENCIA.getMetadadoType());
        Subquery<Integer> subDataCumprimento = createSubqueryMetadado(cb, query, processo,
                ComunicacaoMetadadoProvider.DATA_CUMPRIMENTO.getMetadadoType());
        Predicate where = cb.and(
                cb.equal(metadadoCumprimento.get(MetadadoProcesso_.metadadoType),
                        ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO.getMetadadoType()),
                cb.greaterThanOrEqualTo(
                        cb.function("to_date", Date.class, metadadoCumprimento.get(MetadadoProcesso_.valor)),
                        dataInicio),
                cb.isNotNull(processo.get(Processo_.idJbpm)), cb.isNull(processo.get(Processo_.dataFim)),
                cb.exists(subDataCiencia), cb.exists(subDataCumprimento).not(), cb.exists(subComunicacao));
        if (dataFim != null) {
            where = cb.and(where, cb.lessThanOrEqualTo(
                    cb.function("to_date", Date.class, metadadoCumprimento.get(MetadadoProcesso_.valor)), dataFim));
        }
        return where;
    }

    private Subquery<Integer> createSubqueryFluxosComunicacao(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Processo> processo) {
        Subquery<Integer> subFluxoAtual = query.subquery(Integer.class);
        Root<ProcessInstance> pi = subFluxoAtual.from(ProcessInstance.class);
        Join<ProcessInstance, ProcessDefinition> pd = pi.join("processDefinition", JoinType.INNER);
        subFluxoAtual.where(cb.equal(pi.get("id"), processo.get(Processo_.idJbpm)),
                pd.get("name").in(getFluxosComunicacaoAtuais()));
        subFluxoAtual.select(cb.literal(1));
        return subFluxoAtual;
    }

    protected Subquery<Integer> createSubqueryTipoProcessoComunicacao(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Processo> processo) {
        Subquery<Integer> subComunicacao = query.subquery(Integer.class);
        Root<MetadadoProcesso> metadadoTipoProcesso = subComunicacao.from(MetadadoProcesso.class);
        subComunicacao.where(cb.equal(metadadoTipoProcesso.get(MetadadoProcesso_.processo), processo),
                cb.equal(metadadoTipoProcesso.get(MetadadoProcesso_.metadadoType),
                        EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType()),
                cb.or(cb.equal(metadadoTipoProcesso.get(MetadadoProcesso_.valor), TipoProcesso.COMUNICACAO.toString()),
                        cb.equal(metadadoTipoProcesso.get(MetadadoProcesso_.valor),
                                TipoProcesso.COMUNICACAO_NAO_ELETRONICA.toString())));
        subComunicacao.select(cb.literal(1));
        return subComunicacao;
    }

    protected Subquery<Integer> createSubqueryMetadado(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Processo> processo, String metadadoType) {
        Subquery<Integer> subMetadado = query.subquery(Integer.class);
        Root<MetadadoProcesso> metadado = subMetadado.from(MetadadoProcesso.class);
        subMetadado.where(cb.equal(metadado.get(MetadadoProcesso_.processo), processo),
                cb.equal(metadado.get(MetadadoProcesso_.metadadoType), metadadoType));
        subMetadado.select(cb.literal(1));
        return subMetadado;
    }

    private List<String> getFluxosComunicacaoAtuais() {
        List<String> descricaoFluxo = new ArrayList<>();
        Fluxo fluxoComunicacao = fluxoDAO.getFluxoByCodigo(Parametros.CODIGO_FLUXO_COMUNICACAO_ELETRONICA.getValue());
        if (fluxoComunicacao != null) {
            descricaoFluxo.add(fluxoComunicacao.getFluxo());
        }
        Fluxo fluxoComunicacaoNaoEletronica = fluxoDAO
                .getFluxoByCodigo(Parametros.CODIGO_FLUXO_COMUNICACAO_NAO_ELETRONICA.getValue());
        if (fluxoComunicacaoNaoEletronica != null) {
            descricaoFluxo.add(fluxoComunicacaoNaoEletronica.getFluxo());
        }
        return descricaoFluxo;
    }
}