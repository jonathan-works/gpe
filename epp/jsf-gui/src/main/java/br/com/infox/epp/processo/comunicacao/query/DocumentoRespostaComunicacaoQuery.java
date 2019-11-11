package br.com.infox.epp.processo.comunicacao.query;

public interface DocumentoRespostaComunicacaoQuery {
	String PARAM_DOCUMENTO = "documento";
	String PARAM_DOCUMENTO_LIST = "documentos";
	
	String REMOVER_DOCUMENTO_RESPOSTA = "DocumentoRespostaComunicacao.removerDocumentoResposta";
	String REMOVER_DOCUMENTO_RESPOSTA_QUERY = "delete from DocumentoRespostaComunicacao where documento = :" + PARAM_DOCUMENTO;
	
	String GET_COMUNICACAO_VINCULADA = "DocumentoRespostaComunicacao.getComunicacaoVinculada";
	String GET_COMUNICACAO_VINCULADA_QUERY = "select o.comunicacao from DocumentoRespostaComunicacao o where o.documento = :" + PARAM_DOCUMENTO;
	
	String UPDATE_DOCUMENTO_COMO_ENVIADO = "DocumentoRespostaComunicacao.updateDocumentoComoEnviado";
	String UPDATE_DOCUMENTO_COMO_ENVIADO_QUERY = "update DocumentoRespostaComunicacao o set o.enviado = true where o.documento in (:" + PARAM_DOCUMENTO_LIST +" )";
}
