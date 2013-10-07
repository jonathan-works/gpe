package br.com.infox.epp.query;

public interface ProcessoEpaQuery {

	String PARAM_FLUXO = "fluxo";
	
	String LIST_ALL_NOT_ENDED = "listAllProcessoEpaNotEnded";
	
	String LIST_ALL_NOT_ENDED_QUERY = "select o from ProcessoEpa o where " +
								"o.dataFim is null";
	
	String LIST_NOT_ENDED_BY_FLUXO = "listNotEndedByFluxo";
	String LIST_NOT_ENDED_BY_FLUXO_QUERY = 
			"select o from ProcessoEpa o where " +
			" o.naturezaCategoriaFluxo.fluxo = :" + PARAM_FLUXO +
			" and o.dataFim is null";
	
	String TEMPO_GASTO_PROCESSO_EPP_QUERY = "select new map( sum(pet.tempoGasto) as horas, ( select sum(pet2.tempoGasto) " +
			"from ProcessoEpaTarefa pet2 inner join pet2.tarefa t2 " +
			"where t2.tipoPrazo != 'H' and " +
			"pet2.processoEpa.idProcesso = pet.processoEpa.idProcesso " +
			"group by pet2.processoEpa.idProcesso ) as dias ) " +
			"from ProcessoEpaTarefa pet inner join pet.tarefa t " +
			"where t.tipoPrazo = 'H' and pet.processoEpa.idProcesso=:idProcesso " +
			"group by pet.processoEpa.idProcesso";
}