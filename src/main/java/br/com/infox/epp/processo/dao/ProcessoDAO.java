package br.com.infox.epp.processo.dao;

import static br.com.infox.epp.processo.query.ProcessoQuery.ANULA_ACTOR_ID;
import static br.com.infox.epp.processo.query.ProcessoQuery.ANULA_TODOS_OS_ACTOR_IDS;
import static br.com.infox.epp.processo.query.ProcessoQuery.APAGA_ACTOR_ID_DO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.ATUALIZAR_PROCESSOS_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.CAIXA_PARAM;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_LIST_PROCESSO_PARAM;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID;
import static br.com.infox.epp.processo.query.ProcessoQuery.MOVER_PROCESSOS_PARA_CAIXA;
import static br.com.infox.epp.processo.query.ProcessoQuery.MOVER_PROCESSO_PARA_CAIXA;
import static br.com.infox.epp.processo.query.ProcessoQuery.NUMERO_PROCESSO_BY_ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ACTOR_ID;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ID_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ID_TASKMGMINSTANCE;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ID_TOKEN;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVER_PROCESSO_JBMP;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVE_PROCESSO_DA_CAIXA_ATUAL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.ibpm.util.JbpmUtil;

@Name(ProcessoDAO.NAME)
@AutoCreate
public class ProcessoDAO extends DAO<Processo> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoDAO";

    @Transactional(TransactionPropagationType.REQUIRED)
    public void anulaActorId(String actorId) throws DAOException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ACTOR_ID, actorId);
        executeNamedQueryUpdate(ANULA_ACTOR_ID, parameters);
    }

    @Transactional(TransactionPropagationType.REQUIRED)
    public void apagarActorIdDoProcesso(Processo processo) throws DAOException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_PROCESSO, processo.getIdProcesso());
        executeNamedQueryUpdate(APAGA_ACTOR_ID_DO_PROCESSO, parameters);
    }

    @Transactional(TransactionPropagationType.REQUIRED)
    public void anularTodosActorId() throws DAOException {
        executeNamedQueryUpdate(ANULA_TODOS_OS_ACTOR_IDS);
    }

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
}
