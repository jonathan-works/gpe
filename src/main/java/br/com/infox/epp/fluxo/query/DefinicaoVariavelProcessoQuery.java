package br.com.infox.epp.fluxo.query;

public interface DefinicaoVariavelProcessoQuery {
	String QUERY_PARAM_FLUXO = "fluxo";
	String QUERY_PARAM_NOME = "nome";
	
	String LIST_BY_FLUXO_QUERY = "select o from DefinicaoVariavelProcesso o"
			+ " where o.fluxo = :" + QUERY_PARAM_FLUXO + " order by o.nome";
	
	String TOTAL_BY_FLUXO_QUERY = "select count(o) from DefinicaoVariavelProcesso o where o.fluxo = :" + QUERY_PARAM_FLUXO;
	
	String DEFINICAO_BY_FLUXO_NOME_QUERY = "select o from DefinicaoVariavelProcesso o"
			+ " where o.fluxo = :" + QUERY_PARAM_FLUXO + " and o.nome = :" + QUERY_PARAM_NOME;
}
