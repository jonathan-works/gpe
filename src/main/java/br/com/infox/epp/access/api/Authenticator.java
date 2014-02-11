/* $Id: Authenticator.java 1242 2011-01-28 14:44:46Z luizruiz $ */

/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.epp.access.api;

import static br.com.infox.core.constants.WarningConstants.UNCHECKED;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.JpaIdentityStore;

import br.com.infox.core.operation.ChangePasswordOperation;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.dao.UsuarioLocalizacaoDAO;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLocalizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.system.util.ParametroUtil;

@Name(Authenticator.NAME)
@Install(precedence=Install.APPLICATION)
public class Authenticator {
	
	public static final String NAME = "authenticator";

	private static final LogProvider LOG = Logging.getLogProvider(Authenticator.class);
	
	private String newPassword1;
	private String newPassword2;
	private String login;
	
	//Variaveis de sessão
	public static final String PAPEIS_USUARIO_LOGADO = "papeisUsuarioLogado";
	public static final String USUARIO_LOGADO = "usuarioLogado";
	public static final String USUARIO_LOCALIZACAO_ATUAL = "usuarioLogadoLocalizacaoAtual";
	public static final String USUARIO_LOCALIZACAO_LIST = "usuarioLocalizacaoList";
	public static final String INDENTIFICADOR_PAPEL_ATUAL = "identificadorPapelAtual";
	public static final String LOCALIZACOES_FILHAS_ATUAIS = "localizacoesFilhasAtuais";
	public static final String ID_LOCALIZACOES_FILHAS_ATUAIS = "idLocalizacoesFilhasAtuais";

	//Eventos
	public static final String SET_USUARIO_LOCALIZACAO_LIST_EVENT = "authenticator.setUsuarioLocalizacaoListEvent";
	public static final String SET_USUARIO_LOCALIZACAO_EVENT = "authenticator.setUsuarioLocalizacaoEvent";
	
	public String getNewPassword1(){
		return newPassword1;
	}
	
	public static Authenticator instance() {
		return (Authenticator) Component.getInstance(Authenticator.NAME);
	}
	   
	public void setNewPassword1(String newPassword1){
		if (this.newPassword1 != newPassword1 && (this.newPassword1 == null || !this.newPassword1.equals(newPassword1))){
			this.newPassword1 = newPassword1;
	    }      
	}
	
	public String getNewPassword2(){
		return newPassword2;
	}
	   
	public void setNewPassword2(String newPassword2){
		if (newPassword2IsValid(newPassword2)){
			this.newPassword2 = newPassword2;
	    }      
	}

	private boolean newPassword2IsValid(String newPassword2) {
		return this.newPassword2 != newPassword2 && (this.newPassword2 == null || !this.newPassword2.equals(newPassword2));
	}
	
	@Observer(Identity.EVENT_POST_AUTHENTICATE)
	public void postAuthenticate() throws LoginException {
		String id = Identity.instance().getCredentials().getUsername();
		if (id != null) {
			JpaIdentityStore store = getJpaIdentyStore();
			UsuarioLogin usuario = (UsuarioLogin) store.lookupUser(id);
			validaCadastroDeUsuario(id, usuario);
			try {
				getAuthenticatorService().validarUsuario(usuario);
				if (isTrocarSenha()) {
					trocarSenhaUsuario(usuario);
				} else {
					realizarLoginDoUsuario(usuario);
				}
			}
			catch (LoginException e) {
				Identity.instance().unAuthenticate();
				LOG.error("postAuthenticate()",e);
				throw e;
			} catch (DAOException e) {
			    LOG.error("postAuthenticate()",e);
            }
		}
	}

	private void realizarLoginDoUsuario(final UsuarioLogin usuario) throws LoginException {
		getAuthenticatorService().setUsuarioLogadoSessao(usuario);
		obterLocalizacaoAtual(usuario);
		Actor.instance().setId(usuario.getLogin());
	}

	private JpaIdentityStore getJpaIdentyStore() {
		return (JpaIdentityStore) IdentityManager.instance().getIdentityStore();
	}

	private void validaCadastroDeUsuario(String id, UsuarioLogin usuario) throws LoginException {
		if (usuario == null) {
			throw new LoginException("O usuário '" + id + "' não está corretamente cadastrado no sistema.");
		}
	}

	private boolean isTrocarSenha() {
		return newPassword1 != null && !newPassword1.trim().equals("");
	}
	
	private void trocarSenhaUsuario(final UsuarioLogin usuario) throws LoginException, DAOException {
		if (newPassword1.equals(newPassword2)){
		    new ChangePasswordOperation(usuario.getLogin(), newPassword1).run();
			usuario.setProvisorio(false);
			getUsuarioLoginDAO().update(usuario);
			getMessagesHandler().add("Senha alterada com sucesso.");
		} else {
		    throw new LoginException("Nova senha não confere com a confirmação!");
		}
	}

    private FacesMessages getMessagesHandler() {
        return FacesMessages.instance();
    }
	
	public void login(){
		//verificar se o login existe
		Identity identity = Identity.instance();
		Credentials credentials = identity.getCredentials();
		String login = credentials.getUsername();
		UsuarioLogin user = getUsuarioLoginManager().getUsuarioLoginByLogin(login);
		if(user == null) {
			getMessagesHandler().add(Severity.ERROR, "Login inválido.");
			return;
		}
		identity.login();
	}
	
	@Observer(Identity.EVENT_LOGIN_FAILED)
	public void loginFailed(Object obj) throws LoginException {
		UsuarioLogin usuario = getUsuarioLoginManager().getUsuarioLoginByLogin(Identity.instance().getCredentials().getUsername());
		if (usuario != null && !usuario.getAtivo()) {
			throw new LoginException("Este usuário não está ativo.");
		}
		throw new LoginException("Usuário ou senha inválidos.");
	}

    private UsuarioLoginManager getUsuarioLoginManager() {
        return (UsuarioLoginManager) Component.getInstance(UsuarioLoginManager.NAME);
    }
	
	@Observer(Identity.EVENT_LOGGED_OUT)
	public void limparContexto() {
		Credentials credentials = (Credentials) Component.getInstance(Credentials.class);
		credentials.clear();		
		Context context = Contexts.getSessionContext();
		context.remove(USUARIO_LOGADO);
		context.remove(USUARIO_LOCALIZACAO_ATUAL);
		context.remove(PAPEIS_USUARIO_LOGADO);
		context.remove(INDENTIFICADOR_PAPEL_ATUAL);
		context.remove(LOCALIZACOES_FILHAS_ATUAIS);
		context.remove(ID_LOCALIZACOES_FILHAS_ATUAIS);
		context.remove(USUARIO_LOCALIZACAO_LIST);
	}
	
	public static List<Localizacao> getLocalizacoesFilhas(Localizacao localizacao) {
		return getLocalizacoesFilhas(localizacao, new ArrayList<Localizacao>());
	}
	
	private static List<Localizacao> getLocalizacoesFilhas(Localizacao loc, List<Localizacao> list) {
		list.add(loc);
		if (loc.getEstruturaFilho() != null && !list.contains(loc.getEstruturaFilho())) {
			getLocalizacoesFilhas(loc.getEstruturaFilho(), list);
		}
		for (Localizacao locFilho : loc.getLocalizacaoList()) {
			getLocalizacoesFilhas(locFilho, list);
		}
		return list;
	}
	
	public static String getIdsLocalizacoesFilhas(Localizacao localizacao) {
		StringBuilder sb = new StringBuilder();
		List<Localizacao> localizacoesFilhas = getLocalizacoesFilhas(localizacao, new ArrayList<Localizacao>());
		for (Localizacao loc : localizacoesFilhas) {
			if (sb.length() > 0) {
				sb.append(',');
			}
			sb.append(loc.getIdLocalizacao());
		}
		return sb.toString();
	}
	
	public void unAuthenticate() {
		Identity.instance().unAuthenticate();
		limparContexto();
		anulaActorId();
	}
	
	/**
	 * Ao encerrar uma sessao, limpa os processos que o servidor estava trabalhando
	 * 
	 */
	@Observer("org.jboss.seam.preDestroyContext.SESSION")
	public void anulaActorId() {
		String actorId = Actor.instance().getId();
		if (actorId != null) {
			getAuthenticatorService().anulaActorId(actorId);
		}
	}

	/**
	 * Ao ligar a aplicação, limpa todos os actorIds dos processos
	 */
	@Observer("org.jboss.seam.postInitialization")
	public void anulaTodosActorId() {
		getAuthenticatorService().anularTodosActorId();
	}

	private boolean obterLocalizacaoAtual(UsuarioLogin usuario) throws LoginException {
		UsuarioLocalizacao usuarioLocalizacao = getAuthenticatorService().obterLocalizacaoAtual(usuario);
		if (usuarioLocalizacao != null){
			setLocalizacaoAtual(usuarioLocalizacao);
			return true;
		} 
		throw new LoginException("O usuário " + usuario + " não possui Localização");
	}
	
	/**
	 * Muda a localização do usuário logado, removendo todos os roles
	 * da localização anterior (se hover) e atribuindo os roles
	 * da nova localização, recursivamente.
	 * 
	 * @param usuarioLocalizacao
	 */
	public void setLocalizacaoAtual(UsuarioLocalizacao usuarioLocalizacao) {
		Set<String> roleSet = getRolesAtuais(usuarioLocalizacao);
		getAuthenticatorService().removeRolesAntigas();
		getAuthenticatorService().logDaBuscaDasRoles(usuarioLocalizacao);
		getAuthenticatorService().addRolesAtuais(roleSet);
		setVariaveisDoContexto(usuarioLocalizacao, roleSet);
		if (!getUsuarioLogado().getProvisorio() && !isUsuarioExterno()) {
			redirectToPainelDoUsuario();
		}	
	}

	private void redirectToPainelDoUsuario() {
		Redirect redirect = Redirect.instance();
		redirect.getParameters().clear();
		redirect.setViewId("/Painel/list.seam");
		redirect.setParameter("cid", null);
		redirect.execute();
	}

	private void setVariaveisDoContexto(UsuarioLocalizacao usuarioLocalizacao,
			Set<String> roleSet) {
		Contexts.getSessionContext().set(USUARIO_LOCALIZACAO_ATUAL, usuarioLocalizacao);
		Contexts.getSessionContext().set(INDENTIFICADOR_PAPEL_ATUAL, usuarioLocalizacao.getPapel().getIdentificador());
		Contexts.getSessionContext().set(PAPEIS_USUARIO_LOGADO, roleSet);
		Contexts.getSessionContext().set(LOCALIZACOES_FILHAS_ATUAIS, getLocalizacoesFilhas(usuarioLocalizacao.getLocalizacao()));
		Contexts.getSessionContext().remove("mainMenu");
		Contexts.removeFromAllContexts("tarefasTree");
	}

	private Set<String> getRolesAtuais(UsuarioLocalizacao usuarioLocalizacao) {
		return RolesMap.instance().getChildrenRoles(usuarioLocalizacao.getPapel().getIdentificador());
	}

	

	@SuppressWarnings(UNCHECKED)
	public static List<Localizacao> getLocalizacoesFilhasAtuais() {
		return (List<Localizacao>) Contexts.getSessionContext().get(LOCALIZACOES_FILHAS_ATUAIS);
	}
	
	/**
	 * @return a UsuarioLocalizacao atual do usuário logado
	 */
    public static UsuarioLocalizacao getUsuarioLocalizacaoAtual() {
        UsuarioLocalizacao usuarioLocalizacao = (UsuarioLocalizacao) Contexts.getSessionContext().get(
                USUARIO_LOCALIZACAO_ATUAL);
        usuarioLocalizacao = getUsuarioLocalizacaoDAO().find(usuarioLocalizacao.getIdUsuarioLocalizacao());
        return usuarioLocalizacao;
    }
    
    public static boolean isUsuarioAtualResponsavel() {
        return getUsuarioLocalizacaoAtual().getResponsavelLocalizacao();
    }

    private static UsuarioLoginDAO getUsuarioLoginDAO() {
        return (UsuarioLoginDAO) Component.getInstance(UsuarioLoginDAO.NAME);
    }
    
    private static UsuarioLocalizacaoDAO getUsuarioLocalizacaoDAO() {
        return (UsuarioLocalizacaoDAO) Component.getInstance(UsuarioLocalizacaoDAO.NAME);
    }
	
	/**
	 * Atalho para a localização atual
	 * @return localização atual do usuário logado
	 */
	public static Localizacao getLocalizacaoAtual() {
		UsuarioLocalizacao usuarioLocalizacaoAtual = getUsuarioLocalizacaoAtual();
		if (usuarioLocalizacaoAtual != null) {
			return usuarioLocalizacaoAtual.getLocalizacao();
		} 
		return null;
	}

	public static Papel getPapelAtual() {
		UsuarioLocalizacao usuarioLocalizacaoAtual = getUsuarioLocalizacaoAtual();
		if (usuarioLocalizacaoAtual != null) {
			return usuarioLocalizacaoAtual.getPapel();
		} 
		return null;
	}	

	/**
	 * Atalho para o usuario logado
	 * @return usuário logado
	 */
	public static UsuarioLogin getUsuarioLogado() {
		UsuarioLogin usuario = (UsuarioLogin) Contexts.getSessionContext().get("usuarioLogado");
		return getUsuarioLoginDAO().find(usuario.getIdUsuarioLogin());
	}
	
	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
	}

	@SuppressWarnings(UNCHECKED)
	public List<UsuarioLocalizacao> getUsuarioLocalizacaoListItems() {
		List<UsuarioLocalizacao> list = (List<UsuarioLocalizacao>) 
			Contexts.getSessionContext().get(USUARIO_LOCALIZACAO_LIST);
		return list;
	}

	
	public void setLocalizacaoAtualCombo(UsuarioLocalizacao loc) {
		setLocalizacaoAtual(loc);
	}	
	
	public UsuarioLocalizacao getLocalizacaoAtualCombo() {
		return getUsuarioLocalizacaoAtual();
	}
	
	private static AuthenticatorService getAuthenticatorService(){
		return (AuthenticatorService) Component.getInstance(AuthenticatorService.NAME);
	}
	
	public static void loginUsuarioExterno() {
	    Identity identity = Identity.instance();
        Credentials credentials = identity.getCredentials();
        credentials.setUsername(ParametroUtil.LOGIN_USUARIO_EXTERNO);
        credentials.setPassword("");
        identity.quietLogin();
        identity.login();
    }
	
	public boolean isUsuarioExterno() {
        if (Identity.instance().isLoggedIn()) {
            return getUsuarioLogado().getLogin().equals(ParametroUtil.getLoginUsuarioExterno());
        }
        return false;
    }
}
