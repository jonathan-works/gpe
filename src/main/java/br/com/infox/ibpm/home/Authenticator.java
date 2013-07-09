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
package br.com.infox.ibpm.home;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.naming.ldap.LdapContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.security.auth.login.LoginException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.JpaIdentityStore;
import org.jboss.seam.util.Strings;
import org.jboss.ws.extensions.security.SimplePrincipal;

import br.com.infox.access.RolesMap;
import br.com.infox.access.entity.Papel;
import br.com.infox.access.entity.UsuarioLogin;
import br.com.infox.core.certificado.Certificado;
import br.com.infox.core.certificado.CertificadoException;
import br.com.infox.core.certificado.CertificadoLog;
import br.com.infox.core.certificado.DadosCertificado;
import br.com.infox.core.certificado.VerificaCertificado;
import br.com.infox.ibpm.entity.BloqueioUsuario;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.home.UsuarioHome;
import br.com.infox.ibpm.manager.BloqueioUsuarioManager;
import br.com.infox.ibpm.manager.UsuarioLoginManager;
import br.com.infox.ibpm.service.AuthenticatorService;
import br.com.infox.ibpm.entity.UsuarioLocalizacao;
import br.com.infox.ldap.util.LdapUtil;
import br.com.infox.util.ParametroUtil;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;
import br.com.itx.util.HibernateUtil;
import br.com.itx.util.StringUtil;


@Name(Authenticator.NAME)
@Install(precedence=Install.APPLICATION)
public class Authenticator {
	
	public static final String NAME = "authenticator";
	

	private static final UsuarioLocalizacaoComparator USUARIO_LOCALIZACAO_COMPARATOR = new UsuarioLocalizacaoComparator();
	private static final LogProvider LOG = Logging.getLogProvider(Authenticator.class);
	private String newPassword1;
	private String newPassword2;
	private String login;
	private String assinatura;
	private String certChain;
	private String certChainStringLog;
	
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
	
	@In private BloqueioUsuarioManager bloqueioUsuarioManager;
	@In private UsuarioLoginManager usuarioLoginManager;
	@In private AuthenticatorService service;
	
	public String getNewPassword1(){
		return newPassword1;
	}
	
	public static Authenticator instance() {
		return ComponentUtil.getComponent(NAME);
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
		if (this.newPassword2 != newPassword2 && 
				(this.newPassword2 == null || 
						!this.newPassword2.equals(newPassword2))){
			this.newPassword2 = newPassword2;
	    }      
	}
	
	@Observer(Identity.EVENT_POST_AUTHENTICATE)
	public void postAuthenticate() throws LoginException {
		String id = Identity.instance().getCredentials().getUsername();
		if (id != null) {
			JpaIdentityStore store = (JpaIdentityStore) IdentityManager
					.instance().getIdentityStore();
			UsuarioLogin usuario = (UsuarioLogin) store.lookupUser(id);
			// retorna false caso o usuario do Sistema não esteja ativo
			if (usuario == null) {
				throw new LoginException("O usuário '" + id
						+ "' não está corretamente cadastrado no sistema.");
			}
			if (!Strings.isEmpty(assinatura)) {
				assinatura = null;
			}

			try {
				validateUser(usuario);
				if (isTrocarSenha()) {
					trocarSenhaUsuario(usuario);
				}
				else {
					setUsuarioLogadoSessao(usuario);
					obterLocalizacaoAtual(usuario);
					Actor.instance().setId(usuario.getLogin());
				}
			}
			catch (LoginException e) {
				Identity.instance().unAuthenticate();
				throw e;
			}
		}
	}

	private boolean isTrocarSenha() {
		return newPassword1 != null && !newPassword1.trim().equals("");
	}
	
	private void trocarSenhaUsuario(final UsuarioLogin usuario) throws LoginException {
		if (newPassword1.equals(newPassword2)){
			new RunAsOperation(true) {
				@Override
				public void execute() {
					IdentityManager.instance().changePassword(usuario.getLogin(), newPassword1);
				}
			}.run();
			usuario.setProvisorio(false);
			EntityUtil.flush();
			throw new LoginException("Senha alterada com sucesso.");
		}
		
		throw new LoginException("Nova senha não confere com a confirmação!");		
	}
	
	private void validateUser(UsuarioLogin usuario) throws LoginException {
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
	
	private void autenticaManualmenteNoSeamSecurity(String login, IdentityManager identityManager) {
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
	
	public void login(){
		//verificar se o login existe
		UsuarioHome home = UsuarioHome.instance();
		Identity identity = Identity.instance();
		Credentials credentials = identity.getCredentials();
		String login = credentials.getUsername();
		UsuarioLogin user = home.checkUserByLogin(login);
		if(user == null) {
			FacesMessages.instance().add(Severity.ERROR, "Login inválido.");
			return;
		}
		
		//Autenticação via LDAP
		if(user.getLdap() && ("sim".equalsIgnoreCase(ParametroUtil.getLDAPAuthentication()) ||
							  "yes".equalsIgnoreCase(ParametroUtil.getLDAPAuthentication()))) {
			LdapContext ldap = LdapUtil.autentiqueUsuarioAD(login, credentials.getPassword());
			//Autenticado
			if(ldap != null) {
				IdentityManager identityManager = IdentityManager.instance();
				autenticaManualmenteNoSeamSecurity(user.getLogin(), identityManager);
				Events.instance().raiseEvent(Identity.EVENT_POST_AUTHENTICATE, new Object[1]);
				Events.instance().raiseEvent(Identity.EVENT_LOGIN_SUCCESSFUL, new Object[1]);
			}
			else {
				FacesMessages.instance().add(Severity.ERROR, "Senha inválido.");
				return;
			}
		}
		identity.login();
	}
	
	@Observer(Identity.EVENT_LOGIN_FAILED)
	public void loginFailed(Object obj) throws LoginException {
		UsuarioLogin usuario = getUsuario(Identity.instance().getCredentials().getUsername());
		if (usuario != null && !usuario.getAtivo()) {
			throw new LoginException("Este usuário não está ativo.");
		}
		throw new LoginException("Usuário ou senha inválidos.");
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
		if (loc.getEstruturaFilho() != null) {
			getLocalizacoesFilhas(loc.getEstruturaFilho(), list);
		}
		for (Localizacao locFilho : loc.getLocalizacaoList()) {
			getLocalizacoesFilhas(locFilho, list);
		}
		return list;
	}
	
	private UsuarioLogin getUsuarioByCpf(String cpf) throws LoginException {
		String hql = "select o from Usuario o " +
					 "where o.cpf = :cpf ";
		Query q = EntityUtil.createQuery(hql);
		q.setParameter("cpf", cpf);
		UsuarioLogin usuario = EntityUtil.getSingleResult(q);
		if (usuario == null) {
			throw new LoginException("Não foi possível encontrar um usuário que corresponda a este SmartCard. " +
					"Favor verificar junto com a Secretária Judiciaria os dados de CPF e data de nascimento");
		}
		return usuario;
	}	
	
	public String authenticateSC() throws LoginException {
		String cpf = null;
		try {
			Certificado c = new Certificado(certChain);
			DadosCertificado dadosCertificado = DadosCertificado.parse(c);
			cpf = dadosCertificado.getValor(DadosCertificado.CPF);
			if (Strings.isEmpty(cpf)) {
				throw new LoginException("Cpf não encontrado no cartão.");
			}
			cpf = StringUtil.formartCpf(cpf);
		} catch (Exception e) {
			throw new LoginException(e.getMessage());
		}
		EntityManager em = EntityUtil.getEntityManager();
		UsuarioLogin usuario = getUsuarioByCpf(cpf);
		if (usuario == null) {
			throw new LoginException("Usuário não encontrado");
		} else {
			checkValidadeCertificado(certChain);
			assinatura = StringUtil.replaceQuebraLinha(assinatura);
			if (!usuario.getAtivo()) {
				throw new LoginException("Este usuário não está ativo.");
			}			
		}
		
		String login = usuario.getLogin();
		
		IdentityManager identityManager = IdentityManager.instance();
		boolean userExists = identityManager.getIdentityStore().userExists(login);
		if (userExists) {
			autenticaManualmenteNoSeamSecurity(login, identityManager);
			if (usuario.getCertChain() == null) {
				//TODO isso é temporario ate que todo mundo tenha atualizado o certchain
				usuario.setCertChain(certChain);
				em.merge(usuario);
				EntityUtil.flush(em);
			}
			Events.instance().raiseEvent(Identity.EVENT_POST_AUTHENTICATE, new Object[1]);
			Events.instance().raiseEvent(Identity.EVENT_LOGIN_SUCCESSFUL, new Object[1]);
			return null;
		}
		return null;
	}
	
	private void checkValidadeCertificado(String certChain) throws LoginException {
		try {
			VerificaCertificado.verificaValidadeCertificado(certChain);
			VerificaCertificado.verificaRevogacaoCertificado(certChain);
		} catch (CertificadoException e) {
			LOG.warn("Certificado inválido: " + e.getMessage());
			throw new LoginException(e.getMessage());	
		}
	}
	
	private void checkCertificadoUsuario(UsuarioLogin usuario) throws LoginException {
		String assinaturaUsuario = StringUtil.replaceQuebraLinha(usuario.getAssinatura());
		if (Strings.isEmpty(assinatura) || !assinatura.equals(assinaturaUsuario)) {
			throw new LoginException("Assinatura inválida");
		}
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
	
	@SuppressWarnings("rawtypes")
	public UsuarioLogin getUsuario(String login) {
		String sql = "select o from br.com.infox.access.entity.UsuarioLogin o where o.login = :login";
		EntityManager entityManager = EntityUtil.getEntityManager();
		Query query = entityManager.createQuery(sql);
		query.setParameter("login", login);
		List resultList = query.getResultList();
		if (resultList.size() > 0) {
			return (UsuarioLogin) resultList.get(0);
		} 
		return null;
	}
	
	public void unAuthenticate() {
		Identity.instance().unAuthenticate();
		limparContexto();
		anulaActorId();
	}
	
	/**
	 * Ao encerrar uma sessao, limpa os processos que o servidor estava trabalhando
	 * Obs.: usando session do hibernate pq o EM da erro de transação
	 */
	@Observer("org.jboss.seam.preDestroyContext.SESSION")
	public void anulaActorId() {
		String actorId = Actor.instance().getId();
		if (actorId != null) {
			String query = "update public.tb_processo set nm_actor_id = null " +
			"where nm_actor_id = :actorId";
			HibernateUtil.getSession().createSQLQuery(query)
				.setParameter("actorId", actorId).executeUpdate();
		}
	}
	
	/**
	 * Ao ligar a aplicação, limpa todos os actorIds dos processos
	 */
	@Observer("org.jboss.seam.postInitialization")
	public void anulaTodosActorId() {
		String query = "update public.tb_processo set nm_actor_id = null ";
		HibernateUtil.getSession().createSQLQuery(query)
			.executeUpdate();
	}

	/**
	 * Metodo que coloca o usuario logado na sessão
	 * @param usuario
	 */
	private void setUsuarioLogadoSessao(UsuarioLogin usuario) {
		Contexts.getSessionContext().set(USUARIO_LOGADO, usuario);
		List<UsuarioLocalizacao> usuarioLocalizacaoList = new ArrayList<UsuarioLocalizacao>(
				usuario.getUsuarioLocalizacaoList());
		Collections.sort(usuarioLocalizacaoList, USUARIO_LOCALIZACAO_COMPARATOR);
		Contexts.getSessionContext().set(USUARIO_LOCALIZACAO_LIST, usuarioLocalizacaoList);
		Events.instance().raiseEvent(SET_USUARIO_LOCALIZACAO_LIST_EVENT, usuarioLocalizacaoList);
	}
	
	private boolean obterLocalizacaoAtual(UsuarioLogin usuario) throws LoginException {
		List<UsuarioLocalizacao> listUsuarioLoc = new ArrayList<UsuarioLocalizacao>(usuario.getUsuarioLocalizacaoList()) ;
		Collections.sort(listUsuarioLoc, USUARIO_LOCALIZACAO_COMPARATOR);
		if (listUsuarioLoc.size() > 0) {
			UsuarioLocalizacao loc = listUsuarioLoc.get(0);
			
			EntityManager em = EntityUtil.getEntityManager();
			loc = em.getReference(UsuarioLocalizacao.class, loc.getIdUsuarioLocalizacao());
			setLocalizacaoAtual(loc);
			return true;
		} 
		throw new LoginException("O usuário " + usuario + " não possui Localização");
	}
	
	/**
	 * Muda a localização do usuário logado, removendo todos os roles
	 * da localização anterior (se hover) e atribuindo os roles
	 * da nova localização, recursivamente.
	 * 
	 * @param loc
	 */
	public void setLocalizacaoAtual(UsuarioLocalizacao loc) {
		Set<String> roleSet = (Set<String>) 
				Contexts.getSessionContext().get(PAPEIS_USUARIO_LOGADO);
		if (roleSet != null) {
			for (String r : roleSet) {
				Identity.instance().removeRole(r);
			}
		}
		LOG.warn("Obter role da localizacao: " + loc);
		LOG.warn("Obter role do papel: " + loc.getPapel());
		roleSet = RolesMap.instance().getChildrenRoles(loc.getPapel().getIdentificador());
		for (String r : roleSet) {
			Identity.instance().addRole(r);
		}
		Contexts.getSessionContext().set(USUARIO_LOCALIZACAO_ATUAL, loc);
		Contexts.getSessionContext().set(INDENTIFICADOR_PAPEL_ATUAL, loc.getPapel().getIdentificador());
		Contexts.getSessionContext().set(PAPEIS_USUARIO_LOGADO, roleSet);
		Contexts.getSessionContext().set(LOCALIZACOES_FILHAS_ATUAIS, 
				getLocalizacoesFilhas(loc.getLocalizacao()));
		Contexts.getSessionContext().remove("mainMenu");
		Contexts.removeFromAllContexts("tarefasTree");

		
		if (!getUsuarioLogado().getProvisorio()) {
			Redirect redirect = Redirect.instance();
			redirect.getParameters().clear();
			redirect.setViewId("/Painel/list.seam");
			redirect.setParameter("cid", null);
			redirect.execute();
		}	
	}
	
	
	public static List<Localizacao> getLocalizacoesFilhasAtuais() {
		return (List<Localizacao>) Contexts.getSessionContext().get(LOCALIZACOES_FILHAS_ATUAIS);
	}
	
	/**
	 * @return a UsuarioLocalizacao atual do usuário logado
	 */
    public static UsuarioLocalizacao getUsuarioLocalizacaoAtual() {
        UsuarioLocalizacao usuarioLocalizacao = (UsuarioLocalizacao) Contexts.getSessionContext().get(
                USUARIO_LOCALIZACAO_ATUAL);
        usuarioLocalizacao = EntityUtil.getEntityManager().find(UsuarioLocalizacao.class, 
                usuarioLocalizacao.getIdUsuarioLocalizacao());
        return usuarioLocalizacao;
    }
    
    public static boolean isUsuarioAtualResponsavel() {
        UsuarioLocalizacao usuarioLocalizacao = (UsuarioLocalizacao) Contexts.getSessionContext().get(
                USUARIO_LOCALIZACAO_ATUAL);
        usuarioLocalizacao = EntityUtil.getEntityManager().find(UsuarioLocalizacao.class, 
                usuarioLocalizacao.getIdUsuarioLocalizacao());
        return usuarioLocalizacao.getResponsavelLocalizacao();
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
		return EntityUtil.getEntityManager().find(usuario.getClass(), usuario.getIdPessoa());
	}
	
	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
	}

	public void setAssinatura(String assinatura) {
		this.assinatura = assinatura;
	}

	public String getAssinatura() {
		return assinatura;
	}

	public void setCertChain(String certChain) {
		this.certChain = certChain;
	}

	public String getCertChain() {
		return certChain;
	}	
		
	public void setCertChainStringLog(String certChainStringLog) {
		this.certChainStringLog = certChainStringLog;
	}

	public String getCertChainStringLog() {
		return certChainStringLog;
	}	
	
	public void executeLogCertificadoInvalido() {
		String msg = "Login utilizado: " + login + " / " + certChainStringLog;
		LOG.warn(msg);
		CertificadoLog.executeLog(msg);
	}
	
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
}
