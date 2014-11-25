package br.com.infox.epp.processo.comunicacao.query;

public interface ModeloComunicacaoQuery {
	String PARAM_MODELO_COMUNICACAO = "modeloComunicacao";
	
	String IS_EXPEDIDA = "ModeloComunicacao.isExpedida";
	String IS_EXPEDIDA_QUERY = "select 1 from DestinatarioModeloComunicacao d where"
			+ " d.modeloComunicacao = :" + PARAM_MODELO_COMUNICACAO
			+ " and d.expedido = false";
}
