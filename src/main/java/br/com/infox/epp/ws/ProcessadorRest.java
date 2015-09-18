package br.com.infox.epp.ws;

import java.lang.reflect.Method;
import java.util.List;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.ws.messages.WSMessages;

/**
 * Define um processador padrão para serviços REST, responsável por gerenciar a
 * autenticação por meio de um token
 * 
 * @author paulo
 *
 */
@Provider
@ServerInterceptor
public class ProcessadorRest implements PreProcessInterceptor, AcceptedByMethod {

	/**
	 * Pacote onde esse interceptador será aplicado
	 */
	private static final String PACOTE_INTERCEPTADOR = UsuarioRest.class.getPackage().getName();

	private static final String TOKEN_NAME = "webserviceToken";
	public static final String NOME_TOKEN_HEADER_HTTP = "token";

	@Inject
	private ParametroManager parametroManager;

	private ServerResponse getRespostaForbidden() throws ValidationException {
		ServerResponse retorno = new ServerResponse();
		retorno.setStatus(Status.UNAUTHORIZED.getStatusCode());
		retorno.setEntity(WSMessages.ME_TOKEN_INVALIDO.codigo());
		return retorno;
	}

	private void validarToken(String token) throws ValidationException {
		String tokenParametro = parametroManager.getValorParametro(TOKEN_NAME);
		if (tokenParametro == null || !tokenParametro.equals(token)) {
			throw new ValidationException(WSMessages.ME_TOKEN_INVALIDO.codigo());
		}
	}

	@Override
	public ServerResponse preProcess(HttpRequest request, ResourceMethod method)
			throws Failure, WebApplicationException {
		List<String> valoresToken = request.getHttpHeaders().getRequestHeader(NOME_TOKEN_HEADER_HTTP);
		if (valoresToken == null || valoresToken.size() == 0) {
			return getRespostaForbidden();
		}
		String valorToken = valoresToken.get(0);
		try {
			validarToken(valorToken);
		} catch (ValidationException e) { //

			return getRespostaForbidden();
		}
		return null;
	}

	/**
	 * Aplica esse interceptador apenas no pacote definido em
	 * {@link #PACOTE_INTERCEPTADOR}
	 */
	@Override
	public boolean accept(@SuppressWarnings("rawtypes") Class classe, Method metodo) {
		if (classe.getPackage().getName().startsWith(PACOTE_INTERCEPTADOR)) {
			return true;
		}
		return false;
	}

}
