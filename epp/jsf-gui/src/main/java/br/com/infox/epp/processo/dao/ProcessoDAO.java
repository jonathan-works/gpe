package br.com.infox.epp.processo.dao;

import static br.com.infox.epp.processo.query.ProcessoQuery.ATUALIZAR_PROCESSOS_QUERY1;
import static br.com.infox.epp.processo.query.ProcessoQuery.ATUALIZAR_PROCESSOS_QUERY2;
import static br.com.infox.epp.processo.query.ProcessoQuery.ATUALIZAR_PROCESSOS_QUERY3;
import static br.com.infox.epp.processo.query.ProcessoQuery.ATUALIZAR_PROCESSOS_QUERY4;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_PROCESSO_BY_ID_PROCESSO_AND_ID_USUARIO;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_PROCESSO_BY_NUMERO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_ALL_NOT_ENDED;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_NOT_ENDED_BY_FLUXO;
import static br.com.infox.epp.processo.query.ProcessoQuery.NUMERO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.NUMERO_PROCESSO_BY_ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoQuery.NUMERO_PROCESSO_PARAM;
import static br.com.infox.epp.processo.query.ProcessoQuery.NUMERO_PROCESSO_ROOT_PARAM;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_FLUXO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ID_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ID_TASK;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ID_TASKMGMINSTANCE;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ID_TOKEN;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ID_USUARIO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_SITUACAO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSOS_FILHO_BY_TIPO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSOS_FILHO_NOT_ENDED_BY_TIPO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSO_BY_NUMERO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSO_EPA_BY_ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVER_JBPM_LOG;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVER_PROCESSO_JBMP;
import static br.com.infox.epp.processo.query.ProcessoQuery.TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO;
import static br.com.infox.epp.processo.query.ProcessoQuery.TIPO_PROCESSO_PARAM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.ProcessInstance;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.comunicacao.meioexpedicao.MeioExpedicaoSearch;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.query.ProcessoQuery;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa_;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.entity.Tarefa_;
import br.com.infox.epp.tarefa.type.PrazoEnum;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.ibpm.util.JbpmUtil;

@Stateless
@AutoCreate
@Name(ProcessoDAO.NAME)
public class ProcessoDAO extends DAO<Processo> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoDAO";

    @Inject
    private FluxoDAO fluxoDAO;
    @Inject
    private MeioExpedicaoSearch meioExpedicaoSearch;

    public Processo findProcessosByIdProcessoAndIdUsuario(int idProcesso, Integer idUsuarioLogin, Long idTask) {
        Map<String, Object> parameters = new HashMap<>(3);
        parameters.put(PARAM_ID_PROCESSO, idProcesso);
        parameters.put(PARAM_ID_USUARIO, idUsuarioLogin);
        parameters.put(PARAM_ID_TASK, idTask);
        return getNamedSingleResult(GET_PROCESSO_BY_ID_PROCESSO_AND_ID_USUARIO, parameters);
    }

    @Transactional(TransactionPropagationType.REQUIRED)
    public void atualizarProcessos(Long processDefinitionId, String processoDefinitionName) {
        JbpmUtil.getJbpmSession().createSQLQuery(ATUALIZAR_PROCESSOS_QUERY1)
            .setParameter("processDefinitionName", processoDefinitionName)
            .setParameter("processDefinitionId", processDefinitionId)
            .executeUpdate();
        JbpmUtil.getJbpmSession().createSQLQuery(ATUALIZAR_PROCESSOS_QUERY2)
            .setParameter("processDefinitionName", processoDefinitionName)
            .executeUpdate();
        JbpmUtil.getJbpmSession().createSQLQuery(ATUALIZAR_PROCESSOS_QUERY3)
            .setParameter("processDefinitionName", processoDefinitionName)
            .executeUpdate();
        JbpmUtil.getJbpmSession().createSQLQuery(ATUALIZAR_PROCESSOS_QUERY4)
            .executeUpdate();
    }

    public void removerProcessoJbpm(Integer idProcesso, Long idJbpm, Long idTaskMgmInstance, Long idToken) throws DAOException {
        Map<String, Object> params = new HashMap<>(4);
        params.put(PARAM_ID_JBPM, idJbpm);
        params.put(PARAM_ID_PROCESSO, idProcesso);
        params.put(PARAM_ID_TASKMGMINSTANCE, idTaskMgmInstance);
        params.put(PARAM_ID_TOKEN, idToken);
        executeNamedQueryUpdate(REMOVER_PROCESSO_JBMP, params);
    }

    public void removeLogJbpm(Long idToken) {
        Map<String, Object> params = new HashMap<>(1);
        params.put(PARAM_ID_TOKEN, idToken);
        executeNamedQueryUpdate(REMOVER_JBPM_LOG, params);
    }

    public Object[] getIdTaskMgmInstanceAndIdTokenByidJbpm(Long idJbpm) {
        Map<String, Object> params = new HashMap<>(1);
        params.put(PARAM_ID_JBPM, idJbpm);
        return getNamedSingleResult(GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST, params);
    }

    public String getNumeroProcessoByIdJbpm(Long processInstanceId) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_ID_JBPM, processInstanceId);
        return getNamedSingleResult(NUMERO_PROCESSO_BY_ID_JBPM, params);
    }

    public Processo getProcessoByNumero(String numeroProcesso) {
        Map<String, Object> params = new HashMap<>();
        params.put(NUMERO_PROCESSO_PARAM, numeroProcesso);
        return getNamedSingleResult(PROCESSO_BY_NUMERO, params);
    }

    public List<Processo> listAllNotEnded() {
        return getNamedResultList(LIST_ALL_NOT_ENDED);
    }

    public List<Processo> listNotEnded(Fluxo fluxo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_FLUXO, fluxo);
        return getNamedResultList(LIST_NOT_ENDED_BY_FLUXO, parameters);
    }

    public Processo getProcessoEpaByProcesso(Processo processo) {
        return find(processo.getIdProcesso());
    }

    public Processo getProcessoByIdJbpm(Long idJbpm) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_JBPM, idJbpm);
        return getNamedSingleResult(PROCESSO_EPA_BY_ID_JBPM, parameters);
    }

    public List<PessoaFisica> getPessoaFisicaList() {
        Processo pe = getProcessoByIdJbpm(ProcessInstance.instance().getId());
        List<PessoaFisica> pessoaFisicaList = new ArrayList<>();
        for (ParticipanteProcesso participante : pe.getParticipantes()) {
            if (participante.getPessoa().getTipoPessoa().equals(TipoPessoaEnum.F)) {
                pessoaFisicaList.add((PessoaFisica) HibernateUtil.removeProxy(participante.getPessoa()));
            }
        }
        return pessoaFisicaList;
    }

    public List<PessoaJuridica> getPessoaJuridicaList() {
        Processo processo = getProcessoByIdJbpm(ProcessInstance.instance().getId());
        List<PessoaJuridica> pessoaJuridicaList = new ArrayList<>();
        for (ParticipanteProcesso participante : processo.getParticipantes()) {
            if (participante.getPessoa().getTipoPessoa().equals(TipoPessoaEnum.J)) {
                pessoaJuridicaList.add((PessoaJuridica) HibernateUtil.removeProxy(participante.getPessoa()));
            }
        }
        return pessoaJuridicaList;
    }

    public boolean hasPartes(Long idJbpm) {
        Processo pe = getProcessoByIdJbpm(idJbpm);
        return (pe != null) && (pe.hasPartes());
    }

    public Map<String, Object> getTempoGasto(Processo processo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = cb.createTupleQuery();
        Root<ProcessoTarefa> pt = criteriaQuery.from(ProcessoTarefa.class);
        Join<?, Tarefa> tarefa = pt.join(ProcessoTarefa_.tarefa, JoinType.INNER);
        Path<PrazoEnum> selectionTipoPrazo = tarefa.get(Tarefa_.tipoPrazo);
        Expression<Long> selectionSumTempoPrazo = cb.sumAsLong(pt.get(ProcessoTarefa_.tempoGasto));
        criteriaQuery.multiselect(selectionTipoPrazo, selectionSumTempoPrazo);
        criteriaQuery.groupBy(selectionTipoPrazo);
        criteriaQuery.where(cb.equal(pt.get(ProcessoTarefa_.processo).get(Processo_.idProcesso), processo.getIdProcesso()));
        List<Tuple> queryResult = getEntityManager().createQuery(criteriaQuery).getResultList();

        Map<String, Object> result = new HashMap<>();
        result.put("horas", 0);
        result.put("dias", 0);
        for (Tuple map : queryResult) {
            PrazoEnum tipoPrazo = map.get(selectionTipoPrazo);
            long sumTempoPrazo = Optional.ofNullable(map.get(selectionSumTempoPrazo)).orElse(0l);
            if (PrazoEnum.D.equals(tipoPrazo)) {
                result.put("dias", sumTempoPrazo);
            } else if (PrazoEnum.H.equals(tipoPrazo)) {
                result.put("horas", (sumTempoPrazo/60));
            }
        }

        return result;
    }

    public Double getMediaTempoGasto(Fluxo fluxo, SituacaoPrazoEnum prazoEnum) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_FLUXO, fluxo);
        parameters.put(PARAM_SITUACAO, prazoEnum);
        return getNamedSingleResult(TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO, parameters);
    }

    public Processo getProcessoEpaByNumeroProcesso(String numeroProcesso) {
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(NUMERO_PROCESSO, numeroProcesso);
        return getNamedSingleResult(GET_PROCESSO_BY_NUMERO_PROCESSO, parameters);
    }

    public Processo persistProcessoComNumero(Processo processo) throws DAOException {
        try {
            processo.setNumeroProcesso("");
            getEntityManager().persist(processo);
            processo.setNumeroProcesso(processo.getIdProcesso().toString());
            getEntityManager().flush();
            return processo;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    public List<Processo> getProcessosFilhoNotEndedByTipo(Processo processo, String tipoProcesso) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(NUMERO_PROCESSO_ROOT_PARAM, processo.getNumeroProcessoRoot());
        parameters.put(TIPO_PROCESSO_PARAM, tipoProcesso);
        return getNamedResultList(PROCESSOS_FILHO_NOT_ENDED_BY_TIPO, parameters);
    }

    public List<Processo> getProcessosFilhosByTipo(Processo processo, String tipoProcesso) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(NUMERO_PROCESSO_ROOT_PARAM, processo.getNumeroProcessoRoot());
        parameters.put(TIPO_PROCESSO_PARAM, tipoProcesso);
        return getNamedResultList(PROCESSOS_FILHO_BY_TIPO, parameters);
    }

    public List<Processo> getProcessosByIdCaixa(Integer idCaixa) {
        Map<String, Object> params = new HashMap<>(1);
        params.put(ProcessoQuery.PARAM_ID_CAIXA, idCaixa);
        return getNamedResultList(ProcessoQuery.PROCESSOS_BY_ID_CAIXA, params);
    }

    public List<Processo> listProcessosComunicacaoAguardandoCiencia() {
        Fluxo fluxoComunicacao = fluxoDAO.getFluxoByCodigo(ParametroUtil.getParametro(Parametros.CODIGO_FLUXO_COMUNICACAO_ELETRONICA.getLabel()));
        Map<String, Object> params = new HashMap<>(2);
        params.put(ProcessoQuery.TIPO_PROCESSO_PARAM, TipoProcesso.COMUNICACAO.toString());
        params.put(ProcessoQuery.MEIO_EXPEDICAO_PARAM, meioExpedicaoSearch.getMeioExpedicaoSistema().getId().toString());
        params.put(ProcessoQuery.QUERY_PARAM_FLUXO_COMUNICACAO, fluxoComunicacao.getFluxo());
        return getNamedResultList(ProcessoQuery.LIST_PROCESSOS_COMUNICACAO_SEM_CIENCIA, params);
    }

    public List<Processo> listProcessosComunicacaoAguardandoCumprimento() {
        Fluxo fluxoComunicacao = fluxoDAO.getFluxoByCodigo(ParametroUtil.getParametro(Parametros.CODIGO_FLUXO_COMUNICACAO_ELETRONICA.getLabel()));
        Map<String, Object> params = new HashMap<>(2);
        params.put(ProcessoQuery.TIPO_PROCESSO_PARAM, TipoProcesso.COMUNICACAO.toString());
        params.put(ProcessoQuery.MEIO_EXPEDICAO_PARAM, meioExpedicaoSearch.getMeioExpedicaoSistema().getId().toString());
        params.put(ProcessoQuery.QUERY_PARAM_FLUXO_COMUNICACAO, fluxoComunicacao.getFluxo());
        return getNamedResultList(ProcessoQuery.LIST_PROCESSOS_COMUNICACAO_SEM_CUMPRIMENTO, params);
    }

}

