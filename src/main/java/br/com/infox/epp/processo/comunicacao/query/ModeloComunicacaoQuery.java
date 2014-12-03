package br.com.infox.epp.processo.comunicacao.query;

import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;

public interface ModeloComunicacaoQuery {
	String PARAM_MODELO_COMUNICACAO = "modeloComunicacao";
	String PARAM_PROCESSO = "processo";
	String PARAM_ID_DESTINATARIO = "idDestinatario";
	
	String IS_EXPEDIDA = "ModeloComunicacao.isExpedida";
	String IS_EXPEDIDA_QUERY = "select 1 from DestinatarioModeloComunicacao d where"
			+ " d.modeloComunicacao = :" + PARAM_MODELO_COMUNICACAO
			+ " and d.expedido = false";
	
	String LIST_BY_PROCESSO = "ModeloComunicacao.listByProcesso";
	String LIST_BY_PROCESSO_QUERY = "select o from ModeloComunicacao o where "
			+ "o.processo = :" + PARAM_PROCESSO + " "
			+ "and not exists (select 1 from DestinatarioModeloComunicacao d where "
			+ "d.modeloComunicacao = o and d.expedido = false)";
	
	String GET_COMUNICACAO_DESTINATARIO = "ModeloComunicacao.getComunicacaoDestinatario";
	String GET_COMUNICACAO_DESTINATARIO_QUERY = "select o from Processo o where "
			+ "exists (select 1 from MetadadoProcesso m where m.processo = o and "
			+ "m.metadadoType = '" + ComunicacaoService.DESTINATARIO + "' and "
			+ "m.valor = :" + PARAM_ID_DESTINATARIO + ")";
}
