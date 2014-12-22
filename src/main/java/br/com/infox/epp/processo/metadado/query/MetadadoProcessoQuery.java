package br.com.infox.epp.processo.metadado.query;

public interface MetadadoProcessoQuery {
	
	String PARAM_PROCESSO = "processo";
	String PARAM_METADADO_TYPE = "metadadoType";
	
	String LIST_METADADO_PROCESSO_VISIVEL_BY_PROCESSO = "listMetadadoProcessoVisivel";
	String LIST_METADADO_PROCESSO_VISIVEL_BY_PROCESSO_QUERY = "select o from MetadadoProcesso o " +
			"where o.processo = :" + PARAM_PROCESSO + " and o.visivel = true ";

	String GET_METADADO = "MetadadoProcesso.getMetadado";
	String GET_METADADO_QUERY = "select o from MetadadoProcesso o where o.metadadoType = :" + PARAM_METADADO_TYPE
			+ " and o.processo = :" + PARAM_PROCESSO;
	
	String REMOVER_METADADO = "MetadadoProcesso.removerMetadado";
	String REMOVER_METADADO_QUERY = "delete from MetadadoProcesso o where o.metadadoType = :" + PARAM_METADADO_TYPE
			+ " and o.processo = :" + PARAM_PROCESSO; 
}
