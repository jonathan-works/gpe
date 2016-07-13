package br.com.infox.epp.usuario.rest;

import static br.com.infox.epp.ws.RestUtils.produceErrorJson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

import br.com.infox.core.exception.SystemException;
import br.com.infox.core.exception.SystemExceptionFactory;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.usuario.UsuarioLoginSearch;
import br.com.infox.jwt.JWT;
import br.com.infox.jwt.JWTErrorCodes;
import br.com.infox.jwt.claims.InfoxPrivateClaims;
import br.com.infox.jwt.verifiers.Verifiers;
import br.com.infox.security.rsa.RSAErrorCodes;
import br.com.infox.security.rsa.RSAUtil;

@Stateless
public class LoginRestService {

    @Inject private ServletRequest servletRequest;
    @Inject private ParametroManager parametroManager;
    @Inject private UsuarioLoginSearch usuarioLoginSearch;
    @Inject private AuthenticatorService authenticatorService;

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

    public String loginWithRSA(String jwt) {
        final UsuarioLogin usuario = authenticateUserWithRSA(jwt);
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

    private UsuarioLogin authenticateUserWithRSA(String jwt) {
        String base64RsaKey = parametroManager.getValorParametro(Parametros.EPP_API_RSA_PUBLIC_KEY.getLabel());
        if (base64RsaKey == null || base64RsaKey.isEmpty()) {
            throw SystemExceptionFactory.create(RSAErrorCodes.INVALID_PRIVATE_KEY_STRUCTURE)
                    .set(Parametros.EPP_API_RSA_PUBLIC_KEY.getLabel(), base64RsaKey);
        }
        byte[] secret = RSAUtil.getPublicKeyFromBase64(base64RsaKey).getEncoded();
        return authenticateUser(jwt, secret);
    }

    private UsuarioLogin authenticateUser(String jwt, byte[] secret) {
        try {
            Map<String, Object> decodedPayload = JWT.parser().setKey(secret).parse(jwt);

            Verifiers.anyOf(InfoxPrivateClaims.CPF, InfoxPrivateClaims.LOGIN).verify(decodedPayload);
            String login = (String) decodedPayload.get(InfoxPrivateClaims.LOGIN.getClaim());
            if (login != null) {
                return usuarioLoginSearch.getUsuarioByLogin(login);
            }
            String cpf = (String) decodedPayload.get(InfoxPrivateClaims.CPF.getClaim());
            if (cpf != null) {
                return usuarioLoginSearch.getUsuarioLoginByCpf(cpf);
            }
        } catch (SystemException e) {
            if (JWTErrorCodes.SIGNATURE_VERIFICATION_ERROR.equals(e.getErrorCode())
                    || JWTErrorCodes.INVALID_RSA_KEY.equals(e.getErrorCode()))
                throw new WebApplicationException(
                        Response.status(Status.UNAUTHORIZED).entity(produceErrorJson(e.getMessage())).build());
            if (JWTErrorCodes.UNSUPPORTED_ALGORITHM.equals(e.getErrorCode()))
                throw new WebApplicationException(
                        Response.status(Status.BAD_REQUEST).entity(produceErrorJson(e.getMessage())).build());

        } catch (IllegalStateException | PersistenceException e) {
            throw new WebApplicationException(
                    Response.status(Status.BAD_REQUEST).entity(produceErrorJson(e.getMessage())).build());
        }
        return null;
    }

    private UsuarioLogin authenticateUser(String jwt) {
        byte[] secret = parametroManager.getValorParametro("authorizationSecret").getBytes(StandardCharsets.UTF_8);
        return authenticateUser(jwt, secret);
    }

}
