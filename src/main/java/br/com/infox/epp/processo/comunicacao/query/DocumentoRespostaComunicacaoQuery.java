package br.com.infox.epp.processo.comunicacao.query;

public interface DocumentoRespostaComunicacaoQuery {
	String PARAM_DOCUMENTO = "documento";
	
	String REMOVER_DOCUMENTO_RESPOSTA = "DocumentoRespostaComunicacao.removerDocumentoResposta";
	String REMOVER_DOCUMENTO_RESPOSTA_QUERY = "delete from DocumentoRespostaComunicacao where documento = :" + PARAM_DOCUMENTO;
	
	String GET_COMUNICACAO_VINCULADA = "DocumentoRespostaComunicacao.getComunicacaoVinculada";
	String GET_COMUNICACAO_VINCULADA_QUERY = "select o.comunicacao from DocumentoRespostaComunicacao o where o.documento = :" + PARAM_DOCUMENTO;
}
