package br.com.infox.epa.query;

public interface HistoricoModeloDocumentoQuery {
	
	String LIST_MODELO_QUERY = "select distinct o.modeloDocumento from HistoricoModeloDocumento o";
	String LIST_USUARIO_QUERY = "select distinct o.usuarioAlteracao from HistoricoModeloDocumento o";

}
