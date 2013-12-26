package br.com.infox.epp.processo.query;

public interface ProcessoEpaQuery {

    String PARAM_FLUXO = "fluxo";
    String QUERY_PARAM_PROCESSO_EPA = "processoEpa";

    String LIST_ALL_NOT_ENDED = "listAllProcessoEpaNotEnded";

    String LIST_ALL_NOT_ENDED_QUERY = "select o from ProcessoEpa o where "
            + "o.dataFim is null";

    String LIST_NOT_ENDED_BY_FLUXO = "listNotEndedByFluxo";
    String LIST_NOT_ENDED_BY_FLUXO_QUERY = "select o from ProcessoEpa o where "
            + " o.naturezaCategoriaFluxo.fluxo = :" + PARAM_FLUXO
            + " and o.dataFim is null";

    String TEMPO_GASTO_PROCESSO_EPP = "tempoGastoPeloProcesso";
    String TEMPO_GASTO_PROCESSO_EPP_QUERY = "select new map( sum(pet.tempoGasto) as horas, ( select sum(pet2.tempoGasto) "
            + "from ProcessoEpaTarefa pet2 inner join pet2.tarefa t2 "
            + "where t2.tipoPrazo != 'H' and "
            + "pet2.processoEpa.idProcesso = pet.processoEpa.idProcesso "
            + "group by pet2.processoEpa.idProcesso ) as dias ) "
            + "from ProcessoEpaTarefa pet inner join pet.tarefa t "
            + "where t.tipoPrazo = 'H' and pet.processoEpa.idProcesso=:idProcesso "
            + "group by pet.processoEpa.idProcesso";

    String DATA_INICIO_PRIMEIRA_TAREFA = "getDataInicioDaPrimeiraTarefa";
    String DATA_INICIO_PRIMEIRA_TAREFA_QUERY = "select pt.dataInicio from ProcessoEpaTarefa pt "
            + "where pt.processoEpa = :" + QUERY_PARAM_PROCESSO_EPA
            + " and pt.dataInicio <= (select min(pt2.dataInicio) from ProcessoEpaTarefa pt2 "
            + "where pt2.processoEpa = pt.processoEpa)";
    
    String PARAM_ID_JBPM = "idJbpm";
    String PROCESSO_EPA_BY_ID_JBPM = "getProcessoEpaByIdJbpm";
    String PROCESSO_EPA_BY_ID_JBPM_QUERY = "select pe from ProcessoEpa pe where pe.idJbpm = :" + PARAM_ID_JBPM;
    
    String COUNT_PARTES_ATIVAS_DO_PROCESSO = "countPartesAtivasDoProcesso";
    String COUNT_PARTES_ATIVAS_DO_PROCESSO_QUERY = "select count(*) from ParteProcesso partes where partes.processo = :"
            + QUERY_PARAM_PROCESSO_EPA + " and partes.ativo = true";
}
