package br.com.infox.ibpm.service;

import java.security.Principal;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.ws.extensions.security.SimplePrincipal;

import br.com.infox.access.entity.UsuarioLogin;
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
	
	public void validarUsuario(UsuarioLogin usuario) throws LoginException {
		if (usuario.getBloqueio()){
			if(bloqueioUsuarioManager.liberarUsuarioBloqueado(usuario)){
				bloqueioUsuarioManager.desfazerBloqueioUsuario(usuario);
			} else{
				throwUsuarioBloqueado(usuario);
			}
		} else if (usuario.getProvisorio()){
			if (usuarioLoginManager.usuarioExpirou(usuario)){
				usuarioLoginManager.inativarUsuario(usuario);
				throwUsuarioExpirou(usuario);
			}
		} else if(!usuario.getAtivo()) {
			throwUsuarioInativo(usuario);
		}
	}

	private void throwUsuarioExpirou(UsuarioLogin usuario) throws LoginException {
		throw new LoginException("O usuário " + usuario.getNome() + " expirou. " 
								+ "Por favor, contate o adminstrador do sistema");
	}

	private void throwUsuarioInativo(UsuarioLogin usuario) throws LoginException {
		throw new LoginException("O usuário " + usuario.getNome() + " não está ativo.\n");
	}

	private void throwUsuarioBloqueado(UsuarioLogin usuario) throws LoginException {
		throw new LoginException("O usuário " + usuario.getNome() + " está bloqueado." 
								+ "Por favor, contate o adminstrador do sistema");
	}
	
	public UsuarioLogin getUsuarioByCpf(String cpf) throws LoginException {
		UsuarioLogin usuario = usuarioLoginManager.getUsuarioLoginByCpf(cpf);
		if (usuario == null) {
			throw new LoginException("Não foi possível encontrar um usuário que corresponda a este SmartCard. ");
		}
		return usuario;
	}
}
