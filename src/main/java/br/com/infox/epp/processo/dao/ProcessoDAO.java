package br.com.infox.epp.processo.dao;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static br.com.infox.epp.processo.query.ProcessoQuery.ATUALIZAR_PROCESSOS_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.CAIXA_PARAM;
import static br.com.infox.epp.processo.query.ProcessoQuery.COUNT_PARTES_ATIVAS_DO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_PROCESSO_BY_NUMERO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_LIST_PROCESSO_PARAM;
import static br.com.infox.epp.processo.query.ProcessoQuery.ITEM_DO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_ALL_NOT_ENDED;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_NOT_ENDED_BY_FLUXO;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID;
import static br.com.infox.epp.processo.query.ProcessoQuery.MOVER_PROCESSOS_PARA_CAIXA;
import static br.com.infox.epp.processo.query.ProcessoQuery.MOVER_PROCESSO_PARA_CAIXA;
import static br.com.infox.epp.processo.query.ProcessoQuery.NUMERO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.NUMERO_PROCESSO_BY_ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoQuery.NUMERO_PROCESSO_PARAM;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ACTOR_ID;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_FLUXO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ID_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ID_TASKMGMINSTANCE;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ID_TOKEN;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_SITUACAO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSO_BY_NUMERO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSO_EPA_BY_ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoQuery.QUERY_PARAM_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVER_PROCESSO_JBMP;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVE_PROCESSO_DA_CAIXA_ATUAL;
import static br.com.infox.epp.processo.query.ProcessoQuery.TEMPO_GASTO_PROCESSO_EPP_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.ProcessInstance;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.ibpm.util.JbpmUtil;

@AutoCreate
@Name(ProcessoDAO.NAME)
public class ProcessoDAO extends DAO<Processo> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoDAO";
    private static final LogProvider LOG = Logging.getLogProvider(ProcessoDAO.class);

//    @Transactional(TransactionPropagationType.REQUIRED)
//    public void anulaActorId(String actorId) throws DAOException {
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put(PARAM_ACTOR_ID, actorId);
//        executeNamedQueryUpdate(ANULA_ACTOR_ID, parameters);
//    }

//    @Transactional(TransactionPropagationType.REQUIRED)
//    public void apagarActorIdDoProcesso(Processo processo) throws DAOException {
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put(PARAM_ID_PROCESSO, processo.getIdProcesso());
//        executeNamedQueryUpdate(APAGA_ACTOR_ID_DO_PROCESSO, parameters);
//    }

//    @Transactional(TransactionPropagationType.REQUIRED)
//    public void anularTodosActorId() throws DAOException {
//        executeNamedQueryUpdate(ANULA_TODOS_OS_ACTOR_IDS);
//    }

    @Transactional(TransactionPropagationType.REQUIRED)
    public void moverProcessosParaCaixa(List<Integer> idList, Caixa caixa) throws DAOException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ID_LIST_PROCESSO_PARAM, idList);
        parameters.put(CAIXA_PARAM, caixa);
        executeNamedQueryUpdate(MOVER_PROCESSOS_PARA_CAIXA, parameters);
    }

    @Transactional(TransactionPropagationType.REQUIRED)
    public void moverProcessoParaCaixa(Caixa caixa, Processo processo) throws DAOException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_PROCESSO, processo);
        parameters.put(CAIXA_PARAM, caixa);
        executeNamedQueryUpdate(MOVER_PROCESSO_PARA_CAIXA, parameters);
    }

    @Transactional(TransactionPropagationType.REQUIRED)
    public void removerProcessoDaCaixaAtual(Processo processo) throws DAOException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_PROCESSO, processo.getIdProcesso());
        executeNamedQueryUpdate(REMOVE_PROCESSO_DA_CAIXA_ATUAL, parameters);
    }

    public List<Processo> findProcessosByIdProcessoAndActorId(int idProcesso,
            String login) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_PROCESSO, idProcesso);
        parameters.put(PARAM_ACTOR_ID, login);
        return getNamedResultList(LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID, parameters);
    }

    @Transactional(TransactionPropagationType.REQUIRED)
    public void atualizarProcessos() {
        JbpmUtil.getJbpmSession().createSQLQuery(ATUALIZAR_PROCESSOS_QUERY).executeUpdate();
    }
    
    public void removerProcessoJbpm(Integer idProcesso, Long idJbpm, Long idTaskMgmInstance, Long idToken) 
    		throws DAOException{
		Map<String, Object> params = new HashMap<>(4);
		params.put(PARAM_ID_JBPM, idJbpm);
		params.put(PARAM_ID_PROCESSO, idProcesso);
		params.put(PARAM_ID_TASKMGMINSTANCE, idTaskMgmInstance);
		params.put(PARAM_ID_TOKEN, idToken);
		executeNamedQueryUpdate(REMOVER_PROCESSO_JBMP, params);
	}
    
    public Object[] getIdTaskMgmInstanceAndIdTokenByidJbpm(Long idJbpm){
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
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_FLUXO, fluxo);
        return getNamedResultList(LIST_NOT_ENDED_BY_FLUXO, parameters);
    }

    public Processo getProcessoEpaByProcesso(Processo processo) {
        return find(processo.getIdProcesso());
    }

    private Processo getProcessoEpaByIdJbpm(Long idJbpm) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_JBPM, idJbpm);
        return getNamedSingleResult(PROCESSO_EPA_BY_ID_JBPM, parameters);
    }

    public List<PessoaFisica> getPessoaFisicaList() {
    	Processo pe = getProcessoEpaByIdJbpm(ProcessInstance.instance().getId());
        List<PessoaFisica> pessoaFisicaList = new ArrayList<PessoaFisica>();
        for (ParticipanteProcesso participante : pe.getParticipantes()) {
            if (participante.getPessoa().getTipoPessoa().equals(TipoPessoaEnum.F)) {
                pessoaFisicaList.add((PessoaFisica) HibernateUtil.removeProxy(participante.getPessoa()));
            }
        }
        return pessoaFisicaList;
    }

    public List<PessoaJuridica> getPessoaJuridicaList() {
    	Processo processo = getProcessoEpaByIdJbpm(ProcessInstance.instance().getId());
        List<PessoaJuridica> pessoaJuridicaList = new ArrayList<PessoaJuridica>();
        for (ParticipanteProcesso participante : processo.getParticipantes()) {
            if (participante.getPessoa().getTipoPessoa().equals(TipoPessoaEnum.J)) {
                pessoaJuridicaList.add((PessoaJuridica) HibernateUtil.removeProxy(participante.getPessoa()));
            }
        }
        return pessoaJuridicaList;
    }

    public boolean hasPartes(Processo processo) {
        return hasPartes(processo.getIdJbpm());
    }

    public boolean hasPartes(Long idJbpm) {
    	Processo pe = getProcessoEpaByIdJbpm(ProcessInstance.instance().getId());
        return (pe != null) && (pe.hasPartes());
    }

    /**
     * Quando um processo necessita de partes, não é permitido inativar todas as
     * partes do processo de uma vez. Esse método retorna falso (não há
     * permissão de inativar) se o processo possuir uma única parte ativa no
     * momento.
     * */
    public Boolean podeInativarPartes(Processo processo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(QUERY_PARAM_PROCESSO, processo);
        Long count = (Long) getNamedSingleResult(COUNT_PARTES_ATIVAS_DO_PROCESSO, parameters);
        return count != null && count.compareTo(1L) > 0;
    }

    public Item getItemDoProcesso(int idProcesso) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_PROCESSO, idProcesso);
        return getNamedSingleResult(ITEM_DO_PROCESSO, parameters);
    }

    @SuppressWarnings(UNCHECKED)
    public Map<String, Object> getTempoGasto(Processo processo) {
        Query q = getEntityManager().createQuery(TEMPO_GASTO_PROCESSO_EPP_QUERY).setParameter("idProcesso", processo.getIdProcesso());
        Map<String, Object> result = null;
        try {
            result = (Map<String, Object>) q.getSingleResult();
        } catch (NoResultException e) {
        	LOG.info(".getTempoGasto()", e);
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
    
    @Transactional
    public Processo persistProcessoComNumero(Processo processo) throws DAOException{
    	try {
    		processo.setNumeroProcesso("");
    		getEntityManager().persist(processo);
    		processo.setNumeroProcesso(processo.getIdProcesso().toString());
        	getEntityManager().flush();
            return processo;
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            rollbackTransactionIfNeeded();
        }
    }
}
