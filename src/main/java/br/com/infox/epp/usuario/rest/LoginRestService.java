package br.com.infox.epp.usuario.rest;

import static br.com.infox.epp.ws.RestUtils.produceErrorJson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.seam.servlet.ContextualHttpServletRequest;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.usuario.UsuarioLoginSearch;

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
		HttpServletRequest httpServletRequest = ((HttpServletRequest) servletRequest);
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
		String baseUrl = url.substring(0, url.indexOf(contextPath) + contextPath.length());
		return baseUrl + "/Painel/list.seam";
	}

	public static final String URI_LOGIN = "http://www.infox.com.br/login";
	public static final String URI_CPF = "http://www.infox.com.br/cpf";

	private UsuarioLogin authenticateUser(String jwt) {
		byte[] secret = parametroManager.getValorParametro("authorizationSecret").getBytes(StandardCharsets.UTF_8);

		Map<String, Object> decodedPayload;
		try {
			decodedPayload = new JWTVerifier(secret).verify(jwt);
			if (!decodedPayload.containsKey("exp")) {
				throw new JWTVerifyException("The JWT token must have expiration date");
			}
			if (!decodedPayload.containsKey("iat")) {
				throw new JWTVerifyException("The JWT token must have an issued at date");
			}
			if (!decodedPayload.containsKey(URI_LOGIN) || !decodedPayload.containsKey(URI_CPF)) {
				throw new JWTVerifyException("JWT Token expected one of '" + URI_LOGIN + "' or '" + URI_CPF + "'");
			}
			String login = (String) decodedPayload.get(URI_LOGIN);
			if (login != null) {
				return usuarioLoginSearch.getUsuarioByLogin(login);
			}
			String cpf = (String) decodedPayload.get(URI_CPF);
			if (cpf != null) {
				return usuarioLoginSearch.getUsuarioLoginByCpf(cpf);
			}
		} catch (NoSuchAlgorithmException | SignatureException | JWTVerifyException | InvalidKeyException e) {
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).entity(produceErrorJson(e.getMessage())).build());
		} catch (IllegalStateException | PersistenceException e) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(produceErrorJson(e.getMessage())).build());
		} catch (IOException e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(produceErrorJson(e.getMessage())).build());
		}
		return null;
	}

}
