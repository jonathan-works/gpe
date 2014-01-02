package br.com.infox.epp.tarefa.query;

public interface ProcessoEpaTarefaQuery {

    String QUERY_PARAM_TASKINSTANCE = "taskInstance";
    String QUERY_PARAM_TIPO_PRAZO = "tipoPrazo";

    String GET_PROCESSO_EPA_TAREFA_BY_TASKINSTNACE = "getProcessoEpaTarefaByTaskInstance";
    String GET_PROCESSO_EPA_TAREFA_BY_TASKINSTNACE_QUERY = "select o from ProcessoEpaTarefa o where o.taskInstance = :"
            + QUERY_PARAM_TASKINSTANCE;

    String TAREFA_NOT_ENDED_BY_TIPO_PRAZO = "listAllProcessoEpaTarefaNotEnded";
    String TAREFA_NOT_ENDED_BY_TIPO_PRAZO_QUERY = "select o from ProcessoEpaTarefa o "
            + "where o.dataFim is null and o.tarefa.tipoPrazo = :"
            + QUERY_PARAM_TIPO_PRAZO;

    String TAREFA_ENDED = "listAllProcessoEppTarefaEnded";
    String TAREFA_ENDED_QUERY = "select pet from ProcessoEpaTarefa pet "
            + "where not pet.dataFim is null";

    String BASE_QUERY_FORA_FLUXO = "select f.fluxo, p, t.tarefa, pt from ProcessoEpaTarefa pt "
            + "inner join pt.tarefa t "
            + "inner join pt.processoEpa p "
            + "inner join p.naturezaCategoriaFluxo ncf "
            + "inner join ncf.categoria c " + "inner join ncf.fluxo f ";
    
    String PARAM_CATEGORIA = "categoria";
    String FORA_PRAZO_FLUXO = "listForaPrazoFluxo";
    String FORA_PRAZO_FLUXO_QUERY = BASE_QUERY_FORA_FLUXO + "where p.porcentagem > 100 "
            + "and pt.dataFim is null and c = :" + PARAM_CATEGORIA;
    
    String PARAM_ID_TAREFA = "idTarefa";
    String PARAM_ID_PROCESSO = "idProcesso";
    String PROCESSO_EPA_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA = "findProcessoEpaTarefaByIdProcessoAndIdTarefa";
    String PROCESSO_EPA_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA_QUERY = "select new map(pet.taskInstance as idTaskInstance) " +
            "from ProcessoEpaTarefa pet " +
            "where pet.tarefa.idTarefa=:" + PARAM_ID_TAREFA + 
            " and pet.processoEpa.idProcesso=:" + PARAM_ID_PROCESSO;

}
