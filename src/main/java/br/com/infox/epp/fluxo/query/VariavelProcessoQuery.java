package br.com.infox.epp.fluxo.query;

public interface VariavelProcessoQuery {
	String QUERY_PARAM_FLUXO = "fluxo";
	
	String LIST_BY_FLUXO_QUERY = "select o from VariavelProcesso o where o.fluxo = :" + QUERY_PARAM_FLUXO + " order by o.nome";
	String TOTAL_BY_FLUXO_QUERY = "select count(o) from VariavelProcesso o where o.fluxo = :" + QUERY_PARAM_FLUXO;
}
