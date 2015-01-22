package br.com.infox.epp.webservice.log.query;

public interface LogWebserviceClientQuery {
	String PARAM_ID_LOG = "idLogWsClient";
	
	String GET_REQUISICAO_FROM_LOG = "LogWebserviceClient.getRequisicaoFromLog";
	String GET_REQUISICAO_FROM_LOG_QUERY = "select requisicao from LogWebserviceClient where id = :" + PARAM_ID_LOG;
	
	String GET_RESPOSTA_FROM_LOG = "LogWebserviceClient.getRespostaFromLog";
	String GET_RESPOSTA_FROM_LOG_QUERY = "select resposta from LogWebserviceClient where id = :" + PARAM_ID_LOG;
	
	String GET_INFORMACOES_ADICIONAIS_FROM_LOG = "LogWebserviceClient.getInformacoesAdicionaisFromLog";
	String GET_INFORMACOES_ADICIONAIS_FROM_LOG_QUERY = "select informacoesAdicionais from LogWebserviceClient where id = :" + PARAM_ID_LOG;
}
