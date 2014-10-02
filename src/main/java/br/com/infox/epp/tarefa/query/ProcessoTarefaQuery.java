package br.com.infox.epp.tarefa.query;

public interface ProcessoTarefaQuery {

    String QUERY_PARAM_TASKINSTANCE = "taskInstance";
    String QUERY_PARAM_TIPO_PRAZO = "tipoPrazo";
    String QUERY_PARAM_PROCESSO = "processo";

    String GET_PROCESSO_TAREFA_BY_TASKINSTNACE = "getProcessoTarefaByTaskInstance";
    String GET_PROCESSO_TAREFA_BY_TASKINSTNACE_QUERY = "select o from ProcessoTarefa o where o.taskInstance = :"
            + QUERY_PARAM_TASKINSTANCE;

    String TAREFA_NOT_ENDED_BY_TIPO_PRAZO = "listAllProcessoTarefaNotEnded";
    String TAREFA_NOT_ENDED_BY_TIPO_PRAZO_QUERY = "select o from ProcessoTarefa o "
            + "where o.dataFim is null and o.tarefa.tipoPrazo = :"
            + QUERY_PARAM_TIPO_PRAZO;

    String TAREFA_ENDED = "listAllProcessoEppTarefaEnded";
    String TAREFA_ENDED_QUERY = "select pet from ProcessoTarefa pet "
            + "where not pet.dataFim is null";

    String BASE_QUERY_FORA_FLUXO = "select f.fluxo, p, t.tarefa, pt from ProcessoTarefa pt "
            + "inner join pt.tarefa t "
            + "inner join pt.processoEpa p "
            + "inner join p.naturezaCategoriaFluxo ncf "
            + "inner join ncf.categoria c inner join ncf.fluxo f ";

    String PARAM_CATEGORIA = "categoria";
    String FORA_PRAZO_FLUXO = "listForaPrazoFluxo";
    String FORA_PRAZO_FLUXO_QUERY = BASE_QUERY_FORA_FLUXO
            + "where p.porcentagem > 100 " + "and pt.dataFim is null and c = :"
            + PARAM_CATEGORIA;

    String FORA_PRAZO_TAREFA = "listForaPrazoTarefa";
    String FORA_PRAZO_TAREFA_QUERY = BASE_QUERY_FORA_FLUXO
            + "where pt.porcentagem > 100 "
            + "and pt.dataFim is null and c = :" + PARAM_CATEGORIA;

    String TAREFA_PROXIMA_LIMITE = "listTarefaPertoLimite";
    String TAREFA_PROXIMA_LIMITE_QUERY = BASE_QUERY_FORA_FLUXO
            + "where pt.porcentagem <= 100 "
            + "and pt.porcentagem >= 70 and pt.dataFim is null";

    String PARAM_ID_TAREFA = "idTarefa";
    String PARAM_ID_PROCESSO = "idProcesso";
    String PROCESSO_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA = "findProcessoTarefaByIdProcessoAndIdTarefa";
    String PROCESSO_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA_QUERY = "select new map(pet.taskInstance as idTaskInstance) "
            + "from ProcessoTarefa pet "
            + "where pet.tarefa.idTarefa=:"
            + PARAM_ID_TAREFA
            + " and pet.processoEpa.idProcesso=:"
            + PARAM_ID_PROCESSO;
    
    String DATA_INICIO_PRIMEIRA_TAREFA = "getDataInicioDaPrimeiraTarefa";
    String DATA_INICIO_PRIMEIRA_TAREFA_QUERY = "select pt.dataInicio from ProcessoTarefa pt "
            + "where pt.processo = :" + QUERY_PARAM_PROCESSO
            + " and pt.dataInicio <= (select min(pt2.dataInicio) from ProcessoTarefa pt2 "
            + "where pt2.processoEpa = pt.processoEpa)";

}
