package br.com.infox.epp.usuario.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import org.jboss.seam.servlet.ContextualHttpServletRequest;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.usuario.UsuarioLoginSearch;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;

@Stateless
public class LoginRestService {

	@Inject
	private ServletRequest servletRequest;
	@Inject
	private ParametroManager parametroManager;
	@Inject
	private UsuarioLoginSearch usuarioLoginSearch;
	@Inject
	private AuthenticatorService authenticatorService;

	public String login(String jwt) {
		final UsuarioLogin usuario = authenticateUser(jwt);
		HttpServletRequest httpServletRequest = ((HttpServletRequest)servletRequest);
		try {
			new ContextualHttpServletRequest(httpServletRequest) {
				@Override
				public void process() throws Exception {
					authenticatorService.loginWithoutPassword(usuario);
				}
			}.run();
		} catch (ServletException | IOException e) {
			throw new WebApplicationException(e, 400);
		}
		
		String contextPath = httpServletRequest.getContextPath();
		String url = httpServletRequest.getRequestURL().toString();
		String baseUrl = url.substring(0, url.indexOf(contextPath)+contextPath.length());
		return baseUrl+"/Painel/list.seam";
	}
	
	public static final String URI_LOGIN = "http://www.infox.com.br/login";
	public static final String URI_CPF = "http://www.infox.com.br/cpf";

	private UsuarioLogin authenticateUser(String jwt) {
		String secret = parametroManager.getValorParametro("authorizationSecret");
		final Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(jwt);
		if (claimsJws.getBody().getExpiration() == null){
			throw new UnsupportedJwtException("The JWT token must have expiration date");
		}
		if (claimsJws.getBody().getIssuedAt() == null){
			throw new UnsupportedJwtException("The JWT token must have an issued at date");
		}
		String login = claimsJws.getBody().get(URI_LOGIN, String.class);
		if (login != null) {
			return usuarioLoginSearch.getUsuarioByLogin(login);
		}
		String cpf = claimsJws.getBody().get(URI_CPF, String.class);
		if (cpf != null) {
			return usuarioLoginSearch.getUsuarioLoginByCpf(cpf);
		}
		return null;
	}

}
