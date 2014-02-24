package br.com.infox.epp.processo.dao;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.COUNT_PARTES_ATIVAS_DO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.DATA_INICIO_PRIMEIRA_TAREFA;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.ITEM_DO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.LIST_ALL_NOT_ENDED;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.LIST_NOT_ENDED_BY_FLUXO;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.PARAM_FLUXO;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.PARAM_ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.PARAM_ID_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.PARAM_SITUACAO;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.PROCESSO_EPA_BY_ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.QUERY_PARAM_PROCESSO_EPA;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.TEMPO_GASTO_PROCESSO_EPP_QUERY;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_PROCESSO_BY_NUMERO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.NUMERO_PROCESSO;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.bpm.ProcessInstance;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.partes.entity.ParteProcesso;
import br.com.infox.hibernate.util.HibernateUtil;

/**
 * Classe DAO para a entidade ProcessoEpa
 * 
 * @author Daniel
 * 
 */
@Name(ProcessoEpaDAO.NAME)
@AutoCreate
public class ProcessoEpaDAO extends DAO<ProcessoEpa> {

    private static final long serialVersionUID = 8899227886410190168L;
    private static final LogProvider LOG = Logging.getLogProvider(ProcessoEpaDAO.class);
    public static final String NAME = "processoEpaDAO";

    public List<ProcessoEpa> listAllNotEnded() {
        return getNamedResultList(LIST_ALL_NOT_ENDED);
    }

    public List<ProcessoEpa> listNotEnded(Fluxo fluxo) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_FLUXO, fluxo);
        return getNamedResultList(LIST_NOT_ENDED_BY_FLUXO, parameters);
    }

    public ProcessoEpa getProcessoEpaByProcesso(Processo processo) {
        return find(processo.getIdProcesso());
    }

    private ProcessoEpa getProcessoEpaByIdJbpm(Long idJbpm) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_JBPM, idJbpm);
        return getNamedSingleResult(PROCESSO_EPA_BY_ID_JBPM, parameters);
    }

    public List<PessoaFisica> getPessoaFisicaList() {
        ProcessoEpa pe = getProcessoEpaByIdJbpm(ProcessInstance.instance().getId());
        List<PessoaFisica> pessoaFisicaList = new ArrayList<PessoaFisica>();
        for (ParteProcesso parte : pe.getPartes()) {
            if (parte.getPessoa().getTipoPessoa().equals(TipoPessoaEnum.F)) {
                pessoaFisicaList.add((PessoaFisica) HibernateUtil.removeProxy(parte.getPessoa()));
            }
        }
        return pessoaFisicaList;
    }

    public List<PessoaJuridica> getPessoaJuridicaList() {
        ProcessoEpa pe = getProcessoEpaByIdJbpm(ProcessInstance.instance().getId());
        List<PessoaJuridica> pessoaJuridicaList = new ArrayList<PessoaJuridica>();
        for (ParteProcesso parte : pe.getPartes()) {
            if (parte.getPessoa().getTipoPessoa().equals(TipoPessoaEnum.J)) {
                pessoaJuridicaList.add((PessoaJuridica) HibernateUtil.removeProxy(parte.getPessoa()));
            }
        }
        return pessoaJuridicaList;
    }

    public boolean hasPartes(Processo processo) {
        return hasPartes(processo.getIdJbpm());
    }

    public boolean hasPartes(Long idJbpm) {
        ProcessoEpa pe = getProcessoEpaByIdJbpm(ProcessInstance.instance().getId());
        return (pe != null) && (pe.hasPartes());
    }

    /**
     * Quando um processo necessita de partes, não é permitido inativar todas as
     * partes do processo de uma vez. Esse método retorna falso (não há
     * permissão de inativar) se o processo possuir uma única parte ativa no
     * momento.
     * */
    public Boolean podeInativarPartes(ProcessoEpa processoEpa) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(QUERY_PARAM_PROCESSO_EPA, processoEpa);
        Long count = (Long) getNamedSingleResult(COUNT_PARTES_ATIVAS_DO_PROCESSO, parameters);
        return count != null && count.compareTo(1L) > 0;
    }

    public Item getItemDoProcesso(int idProcesso) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_PROCESSO, idProcesso);
        return getNamedSingleResult(ITEM_DO_PROCESSO, parameters);
    }

    @SuppressWarnings(UNCHECKED)
    public Map<String, Object> getTempoGasto(ProcessoEpa processoEpa) {
        Query q = getEntityManager().createQuery(TEMPO_GASTO_PROCESSO_EPP_QUERY).setParameter("idProcesso", processoEpa.getIdProcesso());
        Map<String, Object> result = null;
        try {
            result = (Map<String, Object>) q.getSingleResult();
        } catch (NoResultException e) {
            LOG.info(".getTempoGasto()", e);
        }
        return result;
    }

    public Date getDataInicioPrimeiraTarefa(ProcessoEpa processoEpa) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(QUERY_PARAM_PROCESSO_EPA, processoEpa);
        return getNamedSingleResult(DATA_INICIO_PRIMEIRA_TAREFA, parameters);
    }
    
    public Double getMediaTempoGasto(Fluxo fluxo, SituacaoPrazoEnum prazoEnum) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_FLUXO, fluxo);
        parameters.put(PARAM_SITUACAO, prazoEnum);
        return getNamedSingleResult(TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO, parameters);
    }
    
    public ProcessoEpa getProcessoEpaByNumeroProcesso(final String numeroProcesso) {
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(NUMERO_PROCESSO, numeroProcesso);
        return getNamedSingleResult(GET_PROCESSO_BY_NUMERO_PROCESSO, parameters);
    }
}
