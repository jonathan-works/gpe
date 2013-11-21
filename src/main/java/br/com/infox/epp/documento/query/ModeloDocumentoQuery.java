package br.com.infox.epp.documento.query;

/**
 * Interface com as queries da entidade ModeloDocumento
 * @author erikliberal
 */
public interface ModeloDocumentoQuery {

	String LIST_ATIVOS = "listModeloDocumentoAtivo";
	String LIST_ATIVOS_QUERY = "select o from ModeloDocumento o " +
								"where o.ativo = true";
	
}