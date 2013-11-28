package br.com.infox.epp.fluxo.query;

public interface DefinicaoVariavelProcessoQuery {
	String QUERY_PARAM_FLUXO = "fluxo";
	
	String LIST_BY_FLUXO_QUERY = "select o from DefinicaoVariavelProcesso o"
			+ " where o.fluxo = :" + QUERY_PARAM_FLUXO + " order by o.nome";
	
	String TOTAL_BY_FLUXO_QUERY = "select count(o) from DefinicaoVariavelProcesso o where o.fluxo = :" + QUERY_PARAM_FLUXO;
}
