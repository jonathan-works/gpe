package br.com.infox.ibpm.service;

import java.security.Principal;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.ws.extensions.security.SimplePrincipal;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.manager.BloqueioUsuarioManager;
import br.com.infox.ibpm.manager.UsuarioLoginManager;

@Name(AuthenticatorService.NAME)
@AutoCreate
public class AuthenticatorService extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "authenticatorService";
	
	@In private UsuarioLoginManager usuarioLoginManager;
	@In private BloqueioUsuarioManager bloqueioUsuarioManager;
	
	public void autenticaManualmenteNoSeamSecurity(String login, IdentityManager identityManager) {
		Principal principal = new SimplePrincipal(login);
		Identity identity = Identity.instance();
		identity.acceptExternallyAuthenticatedPrincipal(principal);
		Credentials credentials = (Credentials) Component.getInstance(Credentials.class);
		credentials.clear();
		credentials.setUsername(login);
		identity.getCredentials().clear();
		identity.getCredentials().setUsername(login);
		List<String> roles = identityManager.getImpliedRoles(login);
		if (roles != null) {
			for (String role : roles) {
				identity.addRole(role);
			}
		}
	}

}
