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
}