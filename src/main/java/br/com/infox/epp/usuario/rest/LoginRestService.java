package br.com.infox.epp.usuario.rest;

import static br.com.infox.epp.ws.RestUtils.produceErrorJson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;
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

import com.auth0.jwt.JWTVerifyException;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.usuario.UsuarioLoginSearch;
import br.com.infox.jwt.JWTUtils;
import br.com.infox.jwt.claims.InfoxPrivateClaims;
import br.com.infox.jwt.verifiers.Verifiers;

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

	private UsuarioLogin authenticateUser(String jwt) {
		byte[] secret = parametroManager.getValorParametro("authorizationSecret").getBytes(StandardCharsets.UTF_8);
		try {
		    Map<String, Object> decodedPayload = JWTUtils.verify(secret, jwt, 
		            Arrays.asList(Verifiers.anyOf(InfoxPrivateClaims.CPF, InfoxPrivateClaims.LOGIN)));
			String login = (String) decodedPayload.get(InfoxPrivateClaims.LOGIN.getClaim());
			if (login != null) {
				return usuarioLoginSearch.getUsuarioByLogin(login);
			}
			String cpf = (String) decodedPayload.get(InfoxPrivateClaims.CPF.getClaim());
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
