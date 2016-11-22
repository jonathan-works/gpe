package br.com.infox.epp.webservice.log.manager;

import java.util.Calendar;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.webservice.log.dao.LogWebserviceClientDAO;
import br.com.infox.epp.webservice.log.entity.LogWebserviceClient;

@Stateless
@Name(LogWebserviceClientManager.NAME)
public class LogWebserviceClientManager extends Manager<LogWebserviceClientDAO, LogWebserviceClient> {

    public static final String NAME = "logWebserviceClientManager";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(LogWebserviceClientManager.class);
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public LogWebserviceClient beginLog(String codigoWebService, String requisicao, String informacoesAdicionais) {
		LogWebserviceClient logWebserviceClient = new LogWebserviceClient();
		logWebserviceClient.setDataInicioRequisicao(Calendar.getInstance().getTime());
		logWebserviceClient.setCodigoWebService(codigoWebService);
		logWebserviceClient.setRequisicao(requisicao);
		logWebserviceClient.setInformacoesAdicionais(informacoesAdicionais);
		try {
			return persist(logWebserviceClient);
		} catch (DAOException e) {
			LOG.error("", e);
		}
		return null;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void endLog(LogWebserviceClient logWebserviceClient, String resposta) {
		logWebserviceClient.setDataFimRequisicao(Calendar.getInstance().getTime());
		logWebserviceClient.setResposta(resposta);
		try {
			update(logWebserviceClient);
		} catch (DAOException e) {
			LOG.error("", e);
		}
	}
	
	public String getRequisicaoFromLog(Long idLog) {
		return getDao().getRequisicaoFromLog(idLog);
	}
	
	public String getInformacoesAdicionaisFromLog(Long idLog) {
		return getDao().getInformacoesAdicionaisFromLog(idLog);
	}
	
	public String getRespostaFromLog(Long idLog) {
		return getDao().getRespostaFromLog(idLog);
	}
}
