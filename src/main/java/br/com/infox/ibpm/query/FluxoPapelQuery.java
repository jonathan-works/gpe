package br.com.infox.ibpm.query;

public interface FluxoPapelQuery {

	String QUERY_PARAM_FLUXO = "fluxo";
	
	String LIST_BY_FLUXO = "listFluxoPapelByFluxo";
	String LIST_BY_FLUXO_QUERY = "select o from FluxoPapel o " +
								 "where o.fluxo = :"+QUERY_PARAM_FLUXO;
	
}