package br.com.infox.epp.query;

public interface HistoricoModeloDocumentoQuery {
	
	String LIST_MODELO_QUERY = "select distinct o.modeloDocumento from HistoricoModeloDocumento o";
	String LIST_USUARIO_PARAM_MODELO = "modeloDocumento";
	String LIST_USUARIO_QUERY = "select distinct o.usuarioAlteracao from HistoricoModeloDocumento o where o.modeloDocumento=:"+LIST_USUARIO_PARAM_MODELO;

}
