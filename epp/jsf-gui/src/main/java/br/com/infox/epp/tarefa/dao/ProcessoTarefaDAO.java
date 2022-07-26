package br.com.infox.epp.tarefa.dao;

import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.DATA_INICIO_PRIMEIRA_TAREFA;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.FORA_PRAZO_FLUXO;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.FORA_PRAZO_TAREFA;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.GET_PROCESSO_TAREFA_BY_TASKINSTNACE;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.PARAM_CATEGORIA;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.PARAM_ID_PROCESSO;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.PARAM_ID_TAREFA;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.PROCESSOS_TAREFA;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.PROCESSO_TAREFA_ABERTO;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.PROCESSO_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.QUERY_PARAM_PROCESSO;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.QUERY_PARAM_TASKINSTANCE;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.QUERY_PARAM_TIPO_PRAZO;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.TAREFA_ENDED;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.TAREFA_NOT_ENDED_BY_TIPO_PRAZO;
import static br.com.infox.epp.tarefa.query.ProcessoTarefaQuery.TAREFA_PROXIMA_LIMITE;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.query.ProcessoTarefaQuery;
import br.com.infox.epp.tarefa.type.PrazoEnum;

@Stateless
@AutoCreate
@Name(ProcessoTarefaDAO.NAME)
public class ProcessoTarefaDAO extends DAO<ProcessoTarefa> {

    private static final long serialVersionUID = 4132828408460655332L;
    public static final String NAME = "processoTarefaDAO";

    public ProcessoTarefa getByTaskInstance(Long taskInstance) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(QUERY_PARAM_TASKINSTANCE, taskInstance);
        return getNamedSingleResult(GET_PROCESSO_TAREFA_BY_TASKINSTNACE, parameters);
    }

    public List<ProcessoTarefa> getTarefaEnded() {
        return getNamedResultList(TAREFA_ENDED);
    }

    public List<ProcessoTarefa> getTarefaNotEnded(PrazoEnum tipoPrazo) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(QUERY_PARAM_TIPO_PRAZO, tipoPrazo);
        return getNamedResultList(TAREFA_NOT_ENDED_BY_TIPO_PRAZO, parameters);
    }

    public List<Object[]> listForaPrazoFluxo(Categoria categoria) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_CATEGORIA, categoria);
        return getNamedSingleResult(FORA_PRAZO_FLUXO, parameters);
    }

    public List<Object[]> listForaPrazoTarefa(Categoria categoria) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_CATEGORIA, categoria);
        return getNamedResultList(FORA_PRAZO_TAREFA, parameters);
    }

    public List<Object[]> listTarefaPertoLimite() {
        return getNamedResultList(TAREFA_PROXIMA_LIMITE);
    }

    public Map<String, Object> findProcessoTarefaByIdProcessoAndIdTarefa(
            final Integer idProcesso, final Integer idTarefa) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_PROCESSO, idProcesso);
        parameters.put(PARAM_ID_TAREFA, idTarefa);
        return getNamedSingleResult(PROCESSO_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA, parameters);
    }
    
    public Date getDataInicioPrimeiraTarefa(Processo processo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(QUERY_PARAM_PROCESSO, processo);
        return getNamedSingleResult(DATA_INICIO_PRIMEIRA_TAREFA, parameters);
    }

    public ProcessoTarefa getProcessoTarefaAberto(Processo processo, Integer idTarefa) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(QUERY_PARAM_PROCESSO, processo);
        parameters.put(ProcessoTarefaQuery.PARAM_ID_TAREFA, idTarefa);
        return getNamedSingleResult(PROCESSO_TAREFA_ABERTO, parameters);
    }

    public List<ProcessoTarefa> getByProcesso(Processo processo) {
        Map<String, Object> params = new HashMap<>();
        params.put(QUERY_PARAM_PROCESSO, processo);
        return getNamedResultList(PROCESSOS_TAREFA, params);
    }
    
    public ProcessoTarefa getUltimoProcessoTarefa(Processo processo) {
    	Map<String, Object> params = new HashMap<>();
        params.put(QUERY_PARAM_PROCESSO, processo);
        return getNamedSingleResult(ProcessoTarefaQuery.ULTIMO_PROCESSO_TAREFA, params);
    }

    public List<ProcessoTarefa> getDoisUltimosProcessosTarefa(Processo processo) {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(QUERY_PARAM_PROCESSO, processo);

        return getResultList(ProcessoTarefaQuery.ULTIMO_PROCESSO_TAREFA_QUERY, parameters, 0, 2);
    }
}
