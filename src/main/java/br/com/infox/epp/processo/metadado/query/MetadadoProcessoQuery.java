package br.com.infox.epp.processo.metadado.query;

public interface MetadadoProcessoQuery {
	
	String PARAM_PROCESSO = "processo";
	
	String LIST_METADADO_PROCESSO_VISIVEL_BY_PROCESSO = "listMetadadoProcessoVisivel";
	String LIST_METADADO_PROCESSO_VISIVEL_BY_PROCESSO_QUERY = "select o from MetadadoProcesso o " +
			"where o.processo = :" + PARAM_PROCESSO + " and o.visivel = true ";

}
