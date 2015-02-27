package br.com.infox.epp.processo.comunicacao.query;


public interface ModeloComunicacaoQuery {
	String PARAM_MODELO_COMUNICACAO = "modeloComunicacao";
	String PARAM_PROCESSO = "processo";
	String PARAM_ID_DESTINATARIO = "idDestinatario";
	String PARAM_IDENTIFICADORES_PAPEL = "identificadoresPapel";
	String PARAM_METADADO_DESTINATARIO = "metadadoDestinatario";
	
	String IS_EXPEDIDA = "ModeloComunicacao.isExpedida";
	String IS_EXPEDIDA_QUERY = "select 1 from DestinatarioModeloComunicacao d where"
			+ " d.modeloComunicacao = :" + PARAM_MODELO_COMUNICACAO
			+ " and d.expedido = false";
	
	String LIST_BY_PROCESSO = "ModeloComunicacao.listByProcesso";
	String LIST_BY_PROCESSO_QUERY = "select o from ModeloComunicacao o where "
			+ "o.processo = :" + PARAM_PROCESSO + " "
			+ "and exists (select 1 from DestinatarioModeloComunicacao d where "
			+ "d.modeloComunicacao = o and d.expedido = true)";
	
	String GET_COMUNICACAO_DESTINATARIO = "ModeloComunicacao.getComunicacaoDestinatario";
	String GET_COMUNICACAO_DESTINATARIO_QUERY = "select o from Processo o where "
			+ "exists (select 1 from MetadadoProcesso m where m.processo = o and "
			+ "m.metadadoType = :" + PARAM_METADADO_DESTINATARIO + " and "
			+ "m.valor = :" + PARAM_ID_DESTINATARIO + ")";
	
	String GET_DOCUMENTO_INCLUSO_POR_PAPEL = "ModeloComunicacao.getDocumentoInclusoPorPapel";
	String GET_DOCUMENTO_INCLUSO_POR_PAPEL_QUERY = "select o from DocumentoModeloComunicacao o "
			+ "inner join o.documento d inner join d.perfilTemplate pt inner join pt.papel p where "
			+ "o.modeloComunicacao = :" + PARAM_MODELO_COMUNICACAO + " "
			+ "and p.identificador in :" + PARAM_IDENTIFICADORES_PAPEL + " "
			+ "order by d.dataInclusao desc";
}
