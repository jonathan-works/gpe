package br.com.infox.epp.ws.interceptors;

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
	private ServletRequest request;

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
		ctx.getContextData().put(LogInterceptor.NOME_PARAMETRO_TOKEN, token);
		validarToken(token);
		return ctx.proceed();
	}

}
