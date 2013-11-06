package br.com.infox.epp.documento.query;

public interface DocumentoFisicoQuery {

	String QUERY_PARAM_PROCESSO = "processo";
	
	String LIST_BY_PROCESSO = "listDocumentoFisicoByProcesso";
	String LIST_BY_PROCESSO_QUERY = "select o from DocumentoFisico o " +
								 "where o.ativo = true and " +
								 "o.processo = :" + QUERY_PARAM_PROCESSO;
	
}