package br.com.infox.epp.ws.services;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.com.infox.epp.webservice.log.entity.LogWebserviceServer;
import br.com.infox.epp.webservice.log.manager.LogWebserviceServerManager;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class LogWebserviceServerManagerNewTransaction {
	
	@Inject
	LogWebserviceServerManager servico;
	
	public LogWebserviceServer beginLog(String webserviceName, String token, String requisicao){
		return servico.beginLog(webserviceName, token, requisicao);
	}
	
	public void endLog(LogWebserviceServer logWebserviceServer, String mensagemRetorno){
		servico.endLog(logWebserviceServer, mensagemRetorno);
	}
	

}
