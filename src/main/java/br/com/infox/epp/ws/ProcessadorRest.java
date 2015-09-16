package br.com.infox.epp.ws;

import java.util.List;

import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.jboss.seam.contexts.Lifecycle;

import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.ws.messages.WSMessages;
import br.com.infox.seam.util.ComponentUtil;

/**
 * Define um processador padrão para serviços REST, responsável por gerenciar a autenticação por meio de um token
 * @author paulo
 *
 */
@Provider
@ServerInterceptor
public class ProcessadorRest implements PreProcessInterceptor {
	
	private ServerResponse getRespostaForbidden() {
		return new ServerResponse(WSMessages.ME_TOKEN_INVALIDO, Status.FORBIDDEN.getStatusCode(), null);
	}

	private static final String TOKEN_NAME = "webserviceToken";
	
	private void validarToken(String token) throws ValidationException {
		Lifecycle.beginCall();
		ParametroManager manager = ComponentUtil.getComponent(ParametroManager.NAME);
		String tokenParametro = manager.getValorParametro(TOKEN_NAME); 
		Lifecycle.endCall();
		if (tokenParametro == null || !tokenParametro.equals(token)){
			throw new ValidationException(WSMessages.ME_TOKEN_INVALIDO.codigo());
		}
	}
	
	@Override
	public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException {
		List<String> valoresToken = request.getHttpHeaders().getRequestHeader("token"); 
		if(valoresToken == null || valoresToken.size() == 0)
		{
			return getRespostaForbidden();
		}
		String valorToken = valoresToken.get(0);
		validarToken(valorToken);
		return null;
	}

}
