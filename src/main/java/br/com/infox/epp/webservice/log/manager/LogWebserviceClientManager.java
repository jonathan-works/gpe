package br.com.infox.epp.webservice.log.manager;

import java.util.Calendar;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.webservice.log.dao.LogWebserviceClientDAO;
import br.com.infox.epp.webservice.log.entity.LogWebserviceClient;

@Stateless
@AutoCreate
@Name(LogWebserviceClientManager.NAME)
public class LogWebserviceClientManager extends PersistenceController {

    public static final String NAME = "logWebserviceClientManager";
	private static final LogProvider LOG = Logging.getLogProvider(LogWebserviceClientManager.class);
	
	@Inject
	private LogWebserviceClientDAO logWebserviceClientDAO;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public LogWebserviceClient beginLog(String codigoWebService, String requisicao, String informacoesAdicionais) {
		LogWebserviceClient logWebserviceClient = new LogWebserviceClient();
		logWebserviceClient.setDataInicioRequisicao(Calendar.getInstance().getTime());
		logWebserviceClient.setCodigoWebService(codigoWebService);
		logWebserviceClient.setRequisicao(requisicao);
		logWebserviceClient.setInformacoesAdicionais(informacoesAdicionais);
		try {
		    getEntityManager().persist(logWebserviceClient);
		    return logWebserviceClient;
		} catch (DAOException e) {
			LOG.error("", e);
		}
		return null;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void endLog(LogWebserviceClient logWebserviceClient, String resposta) {
	    if ( logWebserviceClient != null ) {
	        logWebserviceClient.setDataFimRequisicao(Calendar.getInstance().getTime());
	        logWebserviceClient.setResposta(resposta);
	        try {
	            getEntityManager().merge(logWebserviceClient);
	        } catch (DAOException e) {
	            LOG.error("", e);
	        }
	    }
	}
	
	public String getRequisicaoFromLog(Long idLog) {
		return logWebserviceClientDAO.getRequisicaoFromLog(idLog);
	}
	
	public String getInformacoesAdicionaisFromLog(Long idLog) {
		return logWebserviceClientDAO.getInformacoesAdicionaisFromLog(idLog);
	}
	
	public String getRespostaFromLog(Long idLog) {
		return logWebserviceClientDAO.getRespostaFromLog(idLog);
	}
	
	public EntityManager getEntityManager() {
	    return EntityManagerProducer.instance().getEntityManagerTransactional();
	}
}
