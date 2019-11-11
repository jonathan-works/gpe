package br.com.infox.epp.ws.services;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.epp.webservice.log.entity.LogWebserviceClient;
import br.com.infox.epp.webservice.log.manager.LogWebserviceClientManager;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class LogWebserviceClientManagerNewTransaction {
	
	@Inject
	LogWebserviceClientManager servico;
	
	public LogWebserviceClient beginLog(String codigoWebService, String requisicao, String informacoesAdicionais) {
		return servico.beginLog(codigoWebService, requisicao, informacoesAdicionais);
	}
	
	public void endLog(LogWebserviceClient logWebserviceClient, String resposta) {
		servico.endLog(logWebserviceClient, resposta);
	}
	

}
