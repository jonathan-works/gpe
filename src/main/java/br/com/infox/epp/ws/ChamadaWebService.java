package br.com.infox.epp.ws;

import javax.validation.ValidationException;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Lifecycle;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.webservice.log.entity.LogWebserviceServer;
import br.com.infox.epp.webservice.log.manager.LogWebserviceServerManager;
import br.com.infox.epp.ws.messages.WSMessages;

/**
 * Classe responsável por realizar chamadas a serviços injetando o contexto
 * Seam, fazendo validação e logando no banco de dados
 * 
 * @author paulo
 *
 */
public class ChamadaWebService<T> {

	private WSMessages mensagem;
	private String token;
	private Servico<T> servico;

	public ChamadaWebService(WSMessages mensagem, String token, Servico<T> servico) {
		this.mensagem = mensagem;
		this.token = token;
		this.servico = servico;
	}

	public static interface Servico<V> {
		public String executar(V parametro) throws DAOException;
	}

	public String executar(T parametro) {
		Lifecycle.beginCall();
		ActionMessagesService actionMessagesService = (ActionMessagesService) Component.getInstance(ActionMessagesService.class); 
		LogWebserviceServerManager logWebserviceServerManager = (LogWebserviceServerManager) Component
				.getInstance(LogWebserviceServerManager.class);
		LogWebserviceServer logWsServer = logWebserviceServerManager.beginLog(mensagem.codigo(), token,
				parametro.toString());
		BeanValidator.validate(parametro);
		String retorno = null;
		try {
			retorno = servico.executar(parametro);
		} catch (ValidationException e) {
			retorno = e.getMessage();
		} catch (DAOException e) {
			retorno = actionMessagesService.handleDAOException(e);
		}
		logWebserviceServerManager.endLog(logWsServer, retorno);
		Lifecycle.endCall();
		return retorno;

	}

}
