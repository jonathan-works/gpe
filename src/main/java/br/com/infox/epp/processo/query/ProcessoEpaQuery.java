package br.com.infox.epp.processo.query;

public interface ProcessoEpaQuery {

    String PARAM_FLUXO = "fluxo";
    String QUERY_PARAM_PROCESSO = "processo";

    String LIST_ALL_NOT_ENDED = "listAllProcessoEpaNotEnded";

    String LIST_ALL_NOT_ENDED_QUERY = "select o from Processo o where "
            + "o.dataFim is null";

    String LIST_NOT_ENDED_BY_FLUXO = "listNotEndedByFluxo";
    String LIST_NOT_ENDED_BY_FLUXO_QUERY = "select o from Processo o where "
            + " o.naturezaCategoriaFluxo.fluxo = :" + PARAM_FLUXO
            + " and o.dataFim is null";

    String TEMPO_GASTO_PROCESSO_EPP = "tempoGastoPeloProcesso";
    String TEMPO_GASTO_PROCESSO_EPP_QUERY = "select new map( sum(pet.tempoGasto) / 60 as horas, ( select sum(pet2.tempoGasto) "
            + "from ProcessoTarefa pet2 inner join pet2.tarefa t2 "
            + "where t2.tipoPrazo != 'H' and "
            + "pet2.processo.idProcesso = pet.processo.idProcesso "
            + "group by pet2.processo.idProcesso ) as dias ) "
            + "from ProcessoTarefa pet inner join pet.tarefa t "
            + "where t.tipoPrazo = 'H' and pet.processo.idProcesso = :idProcesso "
            + "group by pet.processo.idProcesso";

    String PARAM_ID_JBPM = "idJbpm";
    String PROCESSO_EPA_BY_ID_JBPM = "getProcessoEpaByIdJbpm";
    String PROCESSO_EPA_BY_ID_JBPM_QUERY = "select pe from Processo pe where pe.idJbpm = :"
            + PARAM_ID_JBPM;

    String COUNT_PARTES_ATIVAS_DO_PROCESSO = "countPartesAtivasDoProcesso";
    String COUNT_PARTES_ATIVAS_DO_PROCESSO_QUERY = "select count(*) from ParticipanteProcesso partes where partes.processo = :"
            + QUERY_PARAM_PROCESSO + " and partes.ativo = true";

    String PARAM_ID_PROCESSO = "idProcesso";
    String ITEM_DO_PROCESSO = "getItemDoProcessoByIdProcesso";
    String ITEM_DO_PROCESSO_QUERY = "select o.itemDoProcesso from Processo o where o.idProcesso =:"
            + PARAM_ID_PROCESSO;

    String PARAM_SITUACAO = "situacao";
    String TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO = "mediaTempoGasto";
    String TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO_QUERY = "select avg(pEpa.tempoGasto) from Processo pEpa "
            + "inner join pEpa.naturezaCategoriaFluxo ncf where ncf.fluxo=:"
            + PARAM_FLUXO
            + " and pEpa.dataFim is null and pEpa.situacaoPrazo=:"
            + PARAM_SITUACAO + " group by ncf.fluxo";
}
