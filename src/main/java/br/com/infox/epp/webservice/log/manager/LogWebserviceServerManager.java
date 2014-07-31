package br.com.infox.epp.webservice.log.manager;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.webservice.log.dao.LogWebserviceServerDAO;
import br.com.infox.epp.webservice.log.entity.LogWebserviceServer;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(LogWebserviceServerManager.NAME)
public class LogWebserviceServerManager extends Manager<LogWebserviceServerDAO, LogWebserviceServer>{

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(LogWebserviceServerManager.class.getName());
	public static final String NAME = "logWebserviceServerManager";
	
	
	public LogWebserviceServer beginLog(String webserviceName, String token, String requisicao){
		LogWebserviceServer logWebserviceServer = new LogWebserviceServer();
		logWebserviceServer.setDatatInicio(Calendar.getInstance().getTime());
		logWebserviceServer.setToken(token);
		logWebserviceServer.setWebService(webserviceName);
		try {
			return persist(logWebserviceServer);
		} catch (DAOException e){
			LOG.log(Level.SEVERE, e.getMessage());
		}
		return null;
	}
	
	public void endLog(LogWebserviceServer logWebserviceServer, String mensagemRetorno){
		logWebserviceServer.setDatatFim(Calendar.getInstance().getTime());
		logWebserviceServer.setMensagemRetorno(mensagemRetorno);
		try {
			update(logWebserviceServer);
		} catch (DAOException e){
			LOG.log(Level.SEVERE, e.getMessage());
		}
	}

}
