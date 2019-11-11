package br.com.infox.epp.ws.interceptors;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import br.com.infox.epp.cdi.util.Beans;
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
	
	private String getMediaType(TipoExcecao tipoExcecao) {
		switch (tipoExcecao) {
		case STRING: return MediaType.TEXT_PLAIN;
		default: return MediaType.APPLICATION_JSON;
		}
	}
	
	@AroundInvoke
	private Object atenticarPorToken(InvocationContext ctx) throws Exception {
		TokenAuthentication anotacao = getAnotacao(ctx);
		
		AutenticadorToken autenticadorToken = Beans.getReference(anotacao.autenticador());
		
		HttpServletRequest req =  ((HttpServletRequest) request);
		String token = autenticadorToken.getValorToken(req);
		logger.finest("Autenticando usando token: '" + token + "'");
		ctx.getContextData().put(LogInterceptor.NOME_PARAMETRO_TOKEN, token);
		
		try
		{
			autenticadorToken.validarToken(req);			
		}
		catch(UnauthorizedException e) {
			String mediaType = getMediaType(anotacao.tipoExcecao());
			e.setMediaType(mediaType);
			throw e;
		}
		
		return ctx.proceed();
	}

}
