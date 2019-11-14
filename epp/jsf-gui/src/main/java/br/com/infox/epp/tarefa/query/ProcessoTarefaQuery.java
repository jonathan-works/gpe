package br.com.infox.epp.tarefa.query;

public interface ProcessoTarefaQuery {

    String QUERY_PARAM_TASKINSTANCE = "taskInstance";
    String QUERY_PARAM_TIPO_PRAZO = "tipoPrazo";
    String QUERY_PARAM_PROCESSO = "processo";
    String QUERY_PARAM_ID_TAREFA = "idTarefa";

    String GET_PROCESSO_TAREFA_BY_TASKINSTNACE = "getProcessoTarefaByTaskInstance";
    String GET_PROCESSO_TAREFA_BY_TASKINSTNACE_QUERY = "select o from ProcessoTarefa o where o.taskInstance = :"
            + QUERY_PARAM_TASKINSTANCE;

    String TAREFA_NOT_ENDED_BY_TIPO_PRAZO = "listAllProcessoTarefaNotEnded";
    String TAREFA_NOT_ENDED_BY_TIPO_PRAZO_QUERY = "select o from ProcessoTarefa o "
    		+ "inner join fetch o.processo p "
    		+ "inner join fetch p.naturezaCategoriaFluxo natCatFluxo "
    		+ "inner join fetch natCatFluxo.fluxo fluxo "
    		+ "inner join fetch o.tarefa tar "
            + "where o.dataFim is null and o.tarefa.tipoPrazo = :"
            + QUERY_PARAM_TIPO_PRAZO;

    String TAREFA_ENDED = "listAllProcessoEppTarefaEnded";
    String TAREFA_ENDED_QUERY = "select pet from ProcessoTarefa pet "
            + "where not pet.dataFim is null";

    String BASE_QUERY_FORA_FLUXO = "select f.fluxo, p, t.tarefa, pt from ProcessoTarefa pt "
            + "inner join pt.tarefa t "
            + "inner join pt.processo p "
            + "inner join p.naturezaCategoriaFluxo ncf "
            + "inner join ncf.categoria c inner join ncf.fluxo f ";

    String PARAM_CATEGORIA = "categoria";
    String FORA_PRAZO_FLUXO = "listForaPrazoFluxo";
    String FORA_PRAZO_FLUXO_QUERY = BASE_QUERY_FORA_FLUXO
            + "where (p.tempoGasto / f.qtPrazo) > 1 " + "and pt.dataFim is null and c = :"
            + PARAM_CATEGORIA;

    String FORA_PRAZO_TAREFA = "listForaPrazoTarefa";
    String FORA_PRAZO_TAREFA_QUERY = BASE_QUERY_FORA_FLUXO
    		+ "where (pt.tempoGasto / pt.tempoPrevisto) > 1 "
            + "and pt.dataFim is null and c = :" + PARAM_CATEGORIA;

    String TAREFA_PROXIMA_LIMITE = "listTarefaPertoLimite";
    String TAREFA_PROXIMA_LIMITE_QUERY = BASE_QUERY_FORA_FLUXO
            + "where (pt.tempoGasto / pt.tempoPrevisto) <= 1 "
            + "and (pt.tempoGasto / pt.tempoPrevisto) >= 0.7 and pt.dataFim is null";

    String PARAM_ID_TAREFA = "idTarefa";
    String PARAM_ID_PROCESSO = "idProcesso";
    String PROCESSO_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA = "findProcessoTarefaByIdProcessoAndIdTarefa";
    String PROCESSO_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA_QUERY = "select new map(pet.taskInstance as idTaskInstance) "
            + "from ProcessoTarefa pet "
            + "where pet.tarefa.idTarefa = :"
            + PARAM_ID_TAREFA
            + " and pet.processo.idProcesso = :"
            + PARAM_ID_PROCESSO
            + " and pet.dataFim = null";
    
    String DATA_INICIO_PRIMEIRA_TAREFA = "getDataInicioDaPrimeiraTarefa";
    String DATA_INICIO_PRIMEIRA_TAREFA_QUERY = "select pt.dataInicio from ProcessoTarefa pt "
            + "where pt.processo = :" + QUERY_PARAM_PROCESSO
            + " and pt.dataInicio <= (select min(pt2.dataInicio) from ProcessoTarefa pt2 "
            + "where pt2.processo = pt.processo)";
    
    String PROCESSO_TAREFA_ABERTO = "ultimoProcessoTarefaByProcesso";
    String PROCESSO_TAREFA_ABERTO_QUERY = "select pt from ProcessoTarefa pt "
            + "where pt.processo = :" + QUERY_PARAM_PROCESSO + " and pt.tarefa.idTarefa = :" + PARAM_ID_TAREFA
            + " and pt.dataFim is null";
    
    String PROCESSOS_TAREFA = "processosTarefa";
    String PROCESSOS_TAREFA_QUERY = "select pt from ProcessoTarefa pt "
    		+ "where pt.processo = :" + QUERY_PARAM_PROCESSO + " order by pt.idProcessoTarefa desc";
    
    String ULTIMO_PROCESSO_TAREFA = "ultimoProcessoTarefa";
    String ULTIMO_PROCESSO_TAREFA_QUERY = "select pt from ProcessoTarefa pt "
    		+ "where pt.processo = :" + QUERY_PARAM_PROCESSO + " order by pt.dataInicio desc";
}
