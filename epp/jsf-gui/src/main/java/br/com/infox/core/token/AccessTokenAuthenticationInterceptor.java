package br.com.infox.core.token;

import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

@AccessTokenAuthentication(TokenRequester.UNSPECIFIED)
@Interceptor
public class AccessTokenAuthenticationInterceptor {

	public static final String NOME_TOKEN_HEADER_HTTP = "EPP-AccessToken";

	@Inject
	private AccessTokenDao accessTokenDao;

	@Inject
	@RequestScoped
	private HttpServletRequest request;
	
	@AroundInvoke
	private Object atenticarPorToken(InvocationContext ctx) throws Exception {
		UUID token = null;
		try {
                    token = UUID.fromString(request.getHeader(NOME_TOKEN_HEADER_HTTP));
		} catch (NullPointerException | IllegalArgumentException e){
		    throw new WebApplicationException(Status.UNAUTHORIZED);
		}
		
		AccessTokenAuthentication annotation = ctx.getMethod().getAnnotation(AccessTokenAuthentication.class);
		if (annotation == null){
		    annotation = ctx.getMethod().getDeclaringClass().getAnnotation(AccessTokenAuthentication.class);
		}
		if (!accessTokenDao.isTokenValid(token, annotation.value())) {
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}
		return ctx.proceed();
	}

}
