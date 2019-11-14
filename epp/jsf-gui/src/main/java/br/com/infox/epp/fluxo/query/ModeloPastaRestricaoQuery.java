package br.com.infox.epp.fluxo.query;

public interface ModeloPastaRestricaoQuery {
	
	String PARAM_MODELO_PASTA = "modeloPasta";
	
	String GET_BY_MODELO_PASTA = "getRestricaoByModeloPasta";
	String GET_BY_MODELO_PASTA_QUERY = "select o from ModeloPastaRestricao o where o.modeloPasta = :" + PARAM_MODELO_PASTA;
	
	String DELETE_BY_MODELO_PASTA = "deleteRestricaoByModeloPasta";
	String DELETE_BY_MODELO_PASTA_QUERY = "delete from ModeloPastaRestricao o where o.modeloPasta = :" + PARAM_MODELO_PASTA;

}
