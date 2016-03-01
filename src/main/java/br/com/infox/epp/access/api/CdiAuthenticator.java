package br.com.infox.epp.access.api;

import java.util.Iterator;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.com.infox.epp.usuario.ExternalAuthenticationService;

@Stateless
public class CdiAuthenticator {

	@Inject
    private Instance<ExternalAuthenticationService> externalAuthenticationService;
	
	public boolean authenticate(String username, String password){
		for (Iterator<ExternalAuthenticationService> it = externalAuthenticationService.iterator(); it.hasNext();) {
			if (it.next().authenticate(username, password)){
				return true;
			}
		}
		return false;
	}
	
}
