package br.com.infox.epp.webservice.log.manager;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.webservice.log.dao.LogWebserviceServerDAO;
import br.com.infox.epp.webservice.log.entity.LogWebserviceServer;

@AutoCreate
@Stateless
@Name(LogWebserviceServerManager.NAME)
public class LogWebserviceServerManager extends Manager<LogWebserviceServerDAO, LogWebserviceServer>{

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(LogWebserviceServerManager.class.getName());
	public static final String NAME = "logWebserviceServerManager";
	
	
	public LogWebserviceServer beginLog(String webserviceName, String token, String requisicao){
		if(token == null || token.isEmpty())
			token = "  ";
		
		if(webserviceName == null || webserviceName.isEmpty())
			webserviceName = "  ";
		
		if(requisicao == null || requisicao.isEmpty())
			requisicao = "  ";
		
		LogWebserviceServer logWebserviceServer = new LogWebserviceServer();
		logWebserviceServer.setDataInicio(Calendar.getInstance().getTime());
		logWebserviceServer.setToken(token);
		logWebserviceServer.setWebService(webserviceName);
		logWebserviceServer.setRequisicao(requisicao);
		try {
			return persist(logWebserviceServer);
		} catch (DAOException e){
			LOG.log(Level.SEVERE, e.getMessage());
		}
		return null;
	}
	
	public void endLog(LogWebserviceServer logWebserviceServer, String mensagemRetorno){
		logWebserviceServer.setDataFim(Calendar.getInstance().getTime());
		logWebserviceServer.setMensagemRetorno(mensagemRetorno);
		try {
			update(logWebserviceServer);
		} catch (DAOException e){
			LOG.log(Level.SEVERE, e.getMessage());
		}
	}
	
	public String getRequisicaoFromLog(BigInteger idLogWsServer){
		String hql = "select requisicao from LogWebserviceServer o where idLogWsServer = :idLogWsServer";
		Map<String, Object> map = new HashMap<>(1);
		map.put("idLogWsServer", idLogWsServer);
		return getDao().getSingleResult(hql, map);
	}

}
