package br.com.infox.epp.julgamento.query;

public interface SalaQuery {
	
	String PARAM_COLEGIADA = "colegiada";
	
	String LIST_SALA_ORDER_BY_NOME = "listSalaOrderByNome";
	String LIST_SALA_ORDER_BY_NOME_QUERY = "select o from Sala o order by nome";
	
	String LIST_SALA_ATIVO_ORDER_BY_NOME = "listSalaAtivoOrderByNome";
	String LIST_SALA_ATIVO_ORDER_BY_NOME_QUERY = "select o from Sala o where o.ativo = true order by nome";
	
	String LIST_SALA_FILTER_BY_COLEGIADA = "listSalaFilterByColegiada";
	String LIST_SALA_FILTER_BY_COLEGIADA_QUERY = "select o from Sala o " +
			"where o.unidadeDecisoraColegiada = :" + PARAM_COLEGIADA + 
			" order by o.nome";
}
