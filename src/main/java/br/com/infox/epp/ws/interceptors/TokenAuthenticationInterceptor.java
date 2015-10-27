package br.com.infox.epp.ws.interceptors;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.ws.exception.UnauthorizedException;
import br.com.infox.epp.ws.messages.WSMessages;

@TokenAuthentication
@Interceptor
public class TokenAuthenticationInterceptor {

	private static final String TOKEN_NAME = "webserviceToken";
	public static final String NOME_TOKEN_HEADER_HTTP = "token";

	@Inject
	private ParametroManager parametroManager;

	@Inject
	@RequestScoped
	private ServletRequest request;
	
	@Inject
	private Logger logger;

	private void validarToken(String token) throws UnauthorizedException {
		String tokenParametro = parametroManager.getValorParametro(TOKEN_NAME);
		if (tokenParametro == null || !tokenParametro.equals(token)) {
			throw new UnauthorizedException(WSMessages.ME_TOKEN_INVALIDO.codigo(),
					WSMessages.ME_TOKEN_INVALIDO.label());
		}
	}

	@AroundInvoke
	private Object atenticarPorToken(InvocationContext ctx) throws Exception {
		String token = ((HttpServletRequest) request).getHeader(NOME_TOKEN_HEADER_HTTP);
		logger.finest("Autenticando usando token: '" + token + "'");
		ctx.getContextData().put(LogInterceptor.NOME_PARAMETRO_TOKEN, token);
		validarToken(token);
		return ctx.proceed();
	}

}
