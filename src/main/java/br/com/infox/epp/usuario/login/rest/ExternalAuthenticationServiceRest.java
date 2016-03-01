package br.com.infox.epp.usuario.login.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.usuario.ExternalAuthenticationService;
import br.com.infox.ws.factory.RestClientFactory;

@Stateless
public class ExternalAuthenticationServiceRest implements ExternalAuthenticationService {

	@Inject
	private ParametroManager parametroManager;

	public boolean authenticate(String username, String password) {
		String token = parametroManager.getValorParametro(Parametros.WEB_SERVICE_TOKEN.getLabel());
		String url = parametroManager.getValorParametro("externalAuthenticationServiceUrl");
		LoginUsuarioRest loginUsuarioRest = RestClientFactory.create(url, br.com.infox.epp.usuario.login.rest.LoginUsuarioRest.class);
		try {
			Response response = loginUsuarioRest.login(token, new LoginUsuarioDTO(username, password));
			if (200 == response.getStatus()){
				return true;
			}
		} catch (WebApplicationException e){
			return false;
		}

		return false;
	}

}
