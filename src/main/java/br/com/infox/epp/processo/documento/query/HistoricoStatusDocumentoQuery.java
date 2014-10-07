package br.com.infox.epp.processo.documento.query;

public interface HistoricoStatusDocumentoQuery {
	
	String PARAM_DOCUMENTO = "documento";
	String PARAM_ID_DOCUMENTO = "idDocumento";
	
	String EXISTE_ALGUM_HISTORICO_BY_ID_DOCUMENTO = "existeAlgumHistoricoByDocumento";
	String EXISTE_ALGUM_HISTORICO_BY_ID_DOCUMENTO_QUERY = "select count(*) from HistoricoStatusDocumento " +
				"where documento.id = :" + PARAM_ID_DOCUMENTO;
	
	String LIST_HISTORICO_BY_DOCUMENTO = "getListHistoricoByDocumento";
	String LIST_HISTORICO_BY_DOCUMENTO_QUERY = "select o from HistoricoStatusDocumento o " +
				"where o.documento = :" + PARAM_DOCUMENTO;

}
