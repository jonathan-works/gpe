package br.com.infox.epp.ws.interceptors;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.rest.RestException;
import br.com.infox.epp.ws.autenticacao.AutenticadorToken;
import br.com.infox.epp.ws.exception.UnauthorizedException;
import br.com.infox.epp.ws.interceptors.TokenAuthentication.TipoExcecao;

@TokenAuthentication
@Interceptor
public class TokenAuthenticationInterceptor {

	@Inject
	@RequestScoped
	private ServletRequest request;
	
	@Inject
	private Logger logger;
	
	private TokenAuthentication getAnotacao(InvocationContext ctx) {
		TokenAuthentication anotacao = ctx.getMethod().getAnnotation(TokenAuthentication.class);
		if(anotacao == null) {
			anotacao = ctx.getMethod().getDeclaringClass().getAnnotation(TokenAuthentication.class);
		}
		return anotacao;
	}
	
	@AroundInvoke
	private Object atenticarPorToken(InvocationContext ctx) throws Exception {
		TokenAuthentication anotacao = getAnotacao(ctx);
		
		AutenticadorToken autenticadorToken = BeanManager.INSTANCE.getReference(anotacao.autenticador());
		
		HttpServletRequest req =  ((HttpServletRequest) request);
		String token = autenticadorToken.getValorToken(req);
		logger.finest("Autenticando usando token: '" + token + "'");
		ctx.getContextData().put(LogInterceptor.NOME_PARAMETRO_TOKEN, token);
		
		try
		{
			autenticadorToken.validarToken(req);			
		}
		catch(UnauthorizedException e) {
			if(anotacao.tipoExcecao() == TipoExcecao.STRING) {
				throw e;
			}
			else
			{
				RestException excecao = new RestException(e.getErro().getCode(), e.getErro().getMessage());
				Response response = Response.status(Status.UNAUTHORIZED).entity(excecao).type(MediaType.APPLICATION_JSON).build();
				throw new WebApplicationException(response);				
			}
		}
		
		return ctx.proceed();
	}

}
