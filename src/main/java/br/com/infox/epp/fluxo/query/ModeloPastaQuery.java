package br.com.infox.epp.fluxo.query;

public interface ModeloPastaQuery {
	String PARAM_FLUXO = "fluxo";
	
	String GET_BY_FLUXO = "getByFluxo";
	String GET_BY_FLUXO_QUERY = "select o from ModeloPasta o where o.fluxo = :" + PARAM_FLUXO + " order by o.ordem";

}
