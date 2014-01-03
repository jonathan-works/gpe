package br.com.infox.epp.processo.dao;

import static br.com.infox.epp.processo.query.ProcessoQuery.ANULA_ACTOR_ID;
import static br.com.infox.epp.processo.query.ProcessoQuery.ANULA_TODOS_OS_ACTOR_IDS;
import static br.com.infox.epp.processo.query.ProcessoQuery.APAGA_ACTOR_ID_DO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.ATUALIZAR_PROCESSOS_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.CAIXA_PARAM;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_LIST_PROCESSO_PARAM;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID;
import static br.com.infox.epp.processo.query.ProcessoQuery.MOVER_PROCESSOS_PARA_CAIXA;
import static br.com.infox.epp.processo.query.ProcessoQuery.MOVER_PROCESSO_PARA_CAIXA;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ACTOR_ID;
import static br.com.infox.epp.processo.query.ProcessoQuery.PARAM_ID_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVE_PROCESSO_DA_CAIXA_ATUAL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.ibpm.util.JbpmUtil;

@Name(ProcessoDAO.NAME)
@AutoCreate
public class ProcessoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoDAO";
	
    public void anulaActorId(String actorId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ACTOR_ID, actorId);
        executeNamedQueryUpdate(ANULA_ACTOR_ID, parameters);
    }

    public void apagarActorIdDoProcesso(Processo processo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_PROCESSO, processo.getIdProcesso());
        executeNamedQueryUpdate(APAGA_ACTOR_ID_DO_PROCESSO, parameters);
	}
	
    @Transactional(TransactionPropagationType.REQUIRED)
    public void anularTodosActorId() {
        executeNamedQueryUpdate(ANULA_TODOS_OS_ACTOR_IDS);
    }

    public void moverProcessosParaCaixa(List<Integer> idList, Caixa caixa) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ID_LIST_PROCESSO_PARAM, idList);
        parameters.put(CAIXA_PARAM, caixa);
        executeNamedQueryUpdate(MOVER_PROCESSOS_PARA_CAIXA, parameters);
    }

    public void moverProcessoParaCaixa(Caixa caixa, Processo processo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_PROCESSO, processo);
        parameters.put(CAIXA_PARAM, caixa);
        executeNamedQueryUpdate(MOVER_PROCESSO_PARA_CAIXA, parameters);
    }

    public void removerProcessoDaCaixaAtual(Processo processo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_PROCESSO, processo.getIdProcesso());
        executeNamedQueryUpdate(REMOVE_PROCESSO_DA_CAIXA_ATUAL, parameters);
    }
	
    public List<Processo> findProcessosByIdProcessoAndActorId(int idProcesso, String login){
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_PROCESSO, idProcesso);
        parameters.put(PARAM_ACTOR_ID, login);
        return getNamedResultList(LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID, parameters);
	}
	
    public void atualizarProcessos() {
        JbpmUtil.getJbpmSession().createSQLQuery(ATUALIZAR_PROCESSOS_QUERY).executeUpdate();
    }
    
}
