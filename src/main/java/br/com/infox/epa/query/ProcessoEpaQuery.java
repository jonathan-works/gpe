package br.com.infox.epa.query;

public interface ProcessoEpaQuery {

	String LIST_ALL_NOT_ENDED = "listAllProcessoEpaNotEnded";
	
	String LIST_ALL_NOT_ENDED_QUERY = "select o from ProcessoEpa o where " +
								"o.dataFim is null";
}