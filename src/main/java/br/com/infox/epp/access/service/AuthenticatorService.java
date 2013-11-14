package br.com.infox.epp.access.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.security.auth.login.LoginException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.SimplePrincipal;
import org.jboss.seam.security.management.IdentityManager;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.access.comparator.UsuarioLocalizacaoComparator;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.BloqueioUsuarioManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.processo.dao.ProcessoDAO;
import br.com.infox.util.constants.WarningConstants;
import br.com.itx.util.EntityUtil;

@Name(AuthenticatorService.NAME)
@AutoCreate
public class AuthenticatorService extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "authenticatorService";
	
	@In private UsuarioLoginManager usuarioLoginManager;
	@In private BloqueioUsuarioManager bloqueioUsuarioManager;
	
	@In private ProcessoDAO processoDAO;
	
	private static final UsuarioLocalizacaoComparator USUARIO_LOCALIZACAO_COMPARATOR = new UsuarioLocalizacaoComparator();
	private static final LogProvider LOG = Logging.getLogProvider(AuthenticatorService.class);
	
	public static final String PAPEIS_USUARIO_LOGADO = "papeisUsuarioLogado";
	public static final String USUARIO_LOGADO = "usuarioLogado";
	public static final String USUARIO_LOCALIZACAO_LIST = "usuarioLocalizacaoList";
	public static final String SET_USUARIO_LOCALIZACAO_LIST_EVENT = "authenticator.setUsuarioLocalizacaoListEvent";
	
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
	
	/**
	 * Metodo que coloca o usuario logado na sessão
	 * @param usuario
	 */
	public void setUsuarioLogadoSessao(UsuarioLogin usuario) {
		Contexts.getSessionContext().set(USUARIO_LOGADO, usuario);
		List<UsuarioLocalizacao> usuarioLocalizacaoList = new ArrayList<UsuarioLocalizacao>(
				usuario.getUsuarioLocalizacaoList());
		Collections.sort(usuarioLocalizacaoList, USUARIO_LOCALIZACAO_COMPARATOR);
		Contexts.getSessionContext().set(USUARIO_LOCALIZACAO_LIST, usuarioLocalizacaoList);
		Events.instance().raiseEvent(SET_USUARIO_LOCALIZACAO_LIST_EVENT, usuarioLocalizacaoList);
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
	
	public UsuarioLogin getUsuarioByLogin(String login){
		return usuarioLoginManager.getUsuarioLoginByLogin(login);
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public void removeRolesAntigas() {
		Set<String> roleSet = (Set<String>) Contexts.getSessionContext().get(PAPEIS_USUARIO_LOGADO);
		if (roleSet != null) {
			for (String r : roleSet) {
				Identity.instance().removeRole(r);
			}
		}
	}
	
	public void logDaBuscaDasRoles(UsuarioLocalizacao usuarioLocalizacao) {
		LOG.warn("Obter role da localizacao: " + usuarioLocalizacao);
		LOG.warn("Obter role do papel: " + usuarioLocalizacao.getPapel());
	}
	
	public void addRolesAtuais(Set<String> roleSet) {
		for (String role : roleSet) {
			Identity.instance().addRole(role);
		}
	}
	
	public UsuarioLocalizacao obterLocalizacaoAtual(UsuarioLogin usuario) throws LoginException {
		List<UsuarioLocalizacao> listUsuarioLoc = new ArrayList<UsuarioLocalizacao>(usuario.getUsuarioLocalizacaoList()) ;
		Collections.sort(listUsuarioLoc, USUARIO_LOCALIZACAO_COMPARATOR);
		if (listUsuarioLoc.size() > 0) {
			UsuarioLocalizacao loc = listUsuarioLoc.get(0);
			EntityManager em = EntityUtil.getEntityManager();
			return em.getReference(UsuarioLocalizacao.class, loc.getIdUsuarioLocalizacao());
		} 
		throw new LoginException("O usuário " + usuario + " não possui Localização");
	}

	public void anulaActorId(String actorId) {
		processoDAO.anulaActorId(actorId);
	}

	public void anularTodosActorId() {
		processoDAO.anularTodosActorId();
	}
	
}
