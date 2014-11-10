/* $Id: Authenticator.java 1242 2011-01-28 14:44:46Z luizruiz $ */

/*
 * IBPM - Ferramenta de produtividade Java Copyright (c) 1986-2009 Infox
 * Tecnologia da Informação Ltda.
 * 
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob
 * os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela Free
 * Software Foundation; versão 2 da Licença. Este programa é distribuído na
 * expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE
 * ESPECÍFICA.
 * 
 * Consulte a GNU GPL para mais detalhes. Você deve ter recebido uma cópia da
 * GNU GPL junto com este programa; se não, veja em http://www.gnu.org/licenses/
 */
package br.com.infox.epp.access.api;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.el.ELException;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.faces.RedirectException;
import org.jboss.seam.international.Messages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.JpaIdentityStore;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.crud.TermoAdesaoAction;
import br.com.infox.epp.access.dao.UsuarioLoginDAO;
import br.com.infox.epp.access.dao.UsuarioPerfilDAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.manager.UsuarioPerfilManager;
import br.com.infox.epp.access.manager.ldap.LDAPManager;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.access.service.PasswordService;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

@Name(Authenticator.NAME)
@Install(precedence = Install.APPLICATION)
public class Authenticator {

    public static final String NAME = "authenticator";

    private static final LogProvider LOG = Logging.getLogProvider(Authenticator.class);

    private String newPassword1;
    private String newPassword2;
    private String login;
    
    @In
    private UsuarioPerfilManager usuarioPerfilManager;

    // Variaveis de sessão
    public static final String PAPEIS_USUARIO_LOGADO = "papeisUsuarioLogado";
    public static final String USUARIO_LOGADO = "usuarioLogado";
    public static final String USUARIO_PERFIL_ATUAL = "usuarioLogadoPerfilAtual";
    public static final String USUARIO_PERFIL_LIST = "usuarioPerfilList";
    public static final String INDENTIFICADOR_PAPEL_ATUAL = "identificadorPapelAtual";
    public static final String LOCALIZACOES_FILHAS_ATUAIS = "localizacoesFilhasAtuais";
    public static final String ID_LOCALIZACOES_FILHAS_ATUAIS = "idLocalizacoesFilhasAtuais";
    public static final String COLEGIADA_DA_MONOCRATICA_LOGADA = "colegiadaDaMonocraticaLogada";

    public String getNewPassword1() {
        return newPassword1;
    }

    public static Authenticator instance() {
        return (Authenticator) Component.getInstance(Authenticator.NAME);
    }

    public void setNewPassword1(String newPassword1) {
        if (this.newPassword1 != newPassword1
                && (this.newPassword1 == null || !this.newPassword1.equals(newPassword1))) {
            this.newPassword1 = newPassword1;
        }
    }

    public String getNewPassword2() {
        return newPassword2;
    }

    public void setNewPassword2(String newPassword2) {
        if (newPassword2IsValid(newPassword2)) {
            this.newPassword2 = newPassword2;
        }
    }

    private boolean newPassword2IsValid(String newPassword2) {
        return this.newPassword2 != newPassword2
                && (this.newPassword2 == null || !this.newPassword2.equals(newPassword2));
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
            } catch (LoginException e) {
                Identity.instance().unAuthenticate();
                LOG.error("postAuthenticate()", e);
                throw e;
            } catch (DAOException e) {
                LOG.error("postAuthenticate()", e);
            }
        }
    }

    public boolean hasToSignTermoAdesao() throws LoginException {
    	UsuarioLogin usuarioLogado = getUsuarioLogado();
    	if (usuarioLogado != null) {
    		return hasToSignTermoAdesao(usuarioLogado);
    	}
    	return false;
    }
    
    private boolean hasToSignTermoAdesao(UsuarioLogin usuario) throws LoginException {
        final List<UsuarioPerfil> perfilAtivoList = usuario.getUsuarioPerfilAtivoList();
        PessoaFisica pessoaFisica = usuario.getPessoaFisica();
        boolean hasToSign = false;
        if (perfilAtivoList != null) {
            for (UsuarioPerfil usuarioPerfil : usuario.getUsuarioPerfilAtivoList()) {
                Papel papel = usuarioPerfil.getPerfilTemplate().getPapel();
                if (papel != null && papel.getTermoAdesao()) {
                    if (pessoaFisica == null) {
                        throw new LoginException("Usuário sem pessoa física associada");
                    }
                    hasToSign = pessoaFisica.getTermoAdesao() == null;
                    break;
                }
            }
        }
        Contexts.getConversationContext().set(TermoAdesaoAction.TERMO_ADESAO_REQ, hasToSign);
        return hasToSign;
    }

    private void realizarLoginDoUsuario(final UsuarioLogin usuario) throws LoginException {
        Contexts.getSessionContext().remove(COLEGIADA_DA_MONOCRATICA_LOGADA);
        getAuthenticatorService().setUsuarioLogadoSessao(usuario);
        obterPerfilAtual(usuario);
        Actor.instance().setId(usuario.getLogin());
    }

    private JpaIdentityStore getJpaIdentyStore() {
        return (JpaIdentityStore) IdentityManager.instance().getIdentityStore();
    }

    private void validaCadastroDeUsuario(String id, UsuarioLogin usuario) throws LoginException {
        if (usuario == null) {
            throw new LoginException("O usuário '" + id
                    + "' não está corretamente cadastrado no sistema.");
        }
    }

    private boolean isTrocarSenha() {
        return newPassword1 != null && !newPassword1.trim().equals("");
    }

    private void trocarSenhaUsuario(final UsuarioLogin usuario) throws LoginException, DAOException {
        if (newPassword1.equals(newPassword2)) {
            PasswordService passwordService = (PasswordService) Component.getInstance(PasswordService.NAME);
            passwordService.changePassword(usuario, newPassword1);
            getMessagesHandler().add("Senha alterada com sucesso.");
        } else {
            throw new LoginException("Nova senha não confere com a confirmação!");
        }
    }

    private FacesMessages getMessagesHandler() {
        return FacesMessages.instance();
    }

    public void login() {
            final Identity identity = Identity.instance();
            final Credentials credentials = identity.getCredentials();
            if (loginExists(credentials) || ldapLoginExists(credentials)) {
                try {
                    identity.login();
                } catch (ELException e) {
                    if (e.getCause() instanceof RedirectException) {
                        LOG.warn("Erro de redirecionamento", e);                        
                    } else {
                        LOG.error(e);
                    }
                }
            } else {
                getMessagesHandler().add(Severity.ERROR, Messages.instance().get("login.error.invalid"));
            }
    }

    private boolean loginExists(final Credentials credentials) {
        final String login = credentials.getUsername();
        final UsuarioLogin user = getUsuarioLoginManager().getUsuarioLoginByLogin(login);
        return user != null;
    }

    private boolean ldapLoginExists(final Credentials credentials) {
        boolean ldapUserExists = false;
        try {
            final LDAPManager ldapManager = (LDAPManager) Component.getInstance(LDAPManager.NAME);
            UsuarioLogin user = ldapManager.autenticarLDAP(credentials.getUsername(), credentials.getPassword(), getProviderUrl(), getDomainName());
            user = getUsuarioLoginManager().persist(user);
            ldapUserExists = user != null;
        } catch (NamingException | DAOException e) {
            LOG.warn("ldapException", e);
        }
        return ldapUserExists;
    }

    private String getDomainName() {
        final ParametroManager parametroManager = (ParametroManager) Component.getInstance(ParametroManager.NAME);
        final Parametro parametro = parametroManager.getParametro("ldapDomainName");
        String result = null;
        if (parametro != null) {
            result = parametro.getValorVariavel();
        } else {
            result = "infoxad.com.br";
        }
        return result;
    }

    private String getProviderUrl() {
        final ParametroManager parametroManager = (ParametroManager) Component.getInstance(ParametroManager.NAME);
        final Parametro parametro = parametroManager.getParametro("ldapProviderUrl");
        String result = null;
        if (parametro != null) {
            result = parametro.getValorVariavel();
        } else {
            result = "ldap://172.20.1.241:389";
        }
        return result;
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
        context.remove(USUARIO_PERFIL_ATUAL);
        context.remove(PAPEIS_USUARIO_LOGADO);
        context.remove(INDENTIFICADOR_PAPEL_ATUAL);
        context.remove(LOCALIZACOES_FILHAS_ATUAIS);
        context.remove(ID_LOCALIZACOES_FILHAS_ATUAIS);
        context.remove(USUARIO_PERFIL_LIST);
        context.remove(COLEGIADA_DA_MONOCRATICA_LOGADA);
    }

    public static List<Localizacao> getLocalizacoesFilhas(
            Localizacao localizacao) {
        return getLocalizacoesFilhas(localizacao, new ArrayList<Localizacao>());
    }

    private static List<Localizacao> getLocalizacoesFilhas(Localizacao loc,
            List<Localizacao> list) {
        list.add(loc);
        if (loc.getEstruturaFilho() != null
                && !list.contains(loc.getEstruturaFilho())) {
//            getLocalizacoesFilhas(loc.getEstruturaFilho(), list);
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

    public void unAuthenticate() throws DAOException {
        Identity.instance().unAuthenticate();
        limparContexto();
        anulaActorId();
    }

    /**
     * Ao encerrar uma sessao, limpa os processos que o servidor estava
     * trabalhando
     * @throws DAOException 
     * 
     */
    @Observer("org.jboss.seam.preDestroyContext.SESSION")
    public void anulaActorId() throws DAOException {
        String actorId = Actor.instance().getId();
        if (actorId != null) {
            getAuthenticatorService().anulaActorId(actorId);
        }
    }

    /**
     * Ao ligar a aplicação, limpa todos os actorIds dos processos
     * @throws DAOException 
     */
    @Observer("org.jboss.seam.postInitialization")
    public void anulaTodosActorId() throws DAOException {
        getAuthenticatorService().anularTodosActorId();
    }

    private boolean obterPerfilAtual(UsuarioLogin usuario) throws LoginException {
        UsuarioPerfil usuarioPerfil = getAuthenticatorService().obterPerfilAtual(usuario);
        if (usuarioPerfil != null) {
            setUsuarioPerfilAtual(usuarioPerfil);
            return true;
        }
        throw new LoginException("O usuário " + usuario
                + " não possui Localização");
    }

    /**
     * Muda a localização do usuário logado, removendo todos os roles da
     * localização anterior (se hover) e atribuindo os roles da nova
     * localização, recursivamente.
     * 
     * @param usuarioPerfil
     * @throws LoginException 
     */
    public void setUsuarioPerfilAtual(UsuarioPerfil usuarioPerfil) throws LoginException {
        Set<String> roleSet = getRolesAtuais(usuarioPerfil);
        Contexts.getSessionContext().remove(COLEGIADA_DA_MONOCRATICA_LOGADA);
        getAuthenticatorService().removeRolesAntigas();
        getAuthenticatorService().logDaBuscaDasRoles(usuarioPerfil);
        getAuthenticatorService().addRolesAtuais(roleSet);
        setVariaveisDoContexto(usuarioPerfil, roleSet);
        if (!getUsuarioLogado().getProvisorio() && !isUsuarioExterno()) {
        	if (!hasToSignTermoAdesao()) {
        		redirectToPainelDoUsuario();
        	} else {
        		redirectToTermoAdesao();
        	}
        }
    }

    private void redirectToPainelDoUsuario() {
        Redirect redirect = Redirect.instance();
        redirect.getParameters().clear();
        redirect.setViewId(getCaminhoPainel());
        redirect.setParameter("cid", null);
        redirect.execute();
    }
    
    protected String getCaminhoPainel() {
        return "/Painel/list.seam";
    }
    
    public String getUrlPainel() {
        return getCaminhoPainel();
    }

    private void redirectToTermoAdesao() {
        Redirect redirect = Redirect.instance();
        redirect.getParameters().clear();
        redirect.setViewId("/termoAdesao.seam");
        redirect.setParameter("cid", null);
        redirect.execute();
    }
    
    private void setVariaveisDoContexto(UsuarioPerfil usuarioPerfil,
            Set<String> roleSet) {
        Contexts.getSessionContext().set(USUARIO_PERFIL_ATUAL, usuarioPerfil);
        Contexts.getSessionContext().set(INDENTIFICADOR_PAPEL_ATUAL, usuarioPerfil.getPerfilTemplate().getPapel().getIdentificador());
        Contexts.getSessionContext().set(PAPEIS_USUARIO_LOGADO, roleSet);
        Contexts.getSessionContext().set(LOCALIZACOES_FILHAS_ATUAIS, getLocalizacoesFilhas(usuarioPerfil.getLocalizacao()));
        Contexts.getSessionContext().remove("mainMenu");
        Contexts.removeFromAllContexts("tarefasTree");
    }

    private Set<String> getRolesAtuais(UsuarioPerfil usuarioPerfil) {
        return RolesMap.instance().getChildrenRoles(usuarioPerfil.getPerfilTemplate().getPapel().getIdentificador());
    }

    @SuppressWarnings(UNCHECKED)
    public static List<Localizacao> getLocalizacoesFilhasAtuais() {
        return (List<Localizacao>) Contexts.getSessionContext().get(LOCALIZACOES_FILHAS_ATUAIS);
    }

    /**
     * @return o UsuarioPerfil atual do usuário logado
     */
    public static UsuarioPerfil getUsuarioPerfilAtual() {
        UsuarioPerfil usuarioPerfil = (UsuarioPerfil) Contexts.getSessionContext().get(USUARIO_PERFIL_ATUAL);
        if (usuarioPerfil != null) {
            usuarioPerfil = getUsuarioPerfilDAO().find(usuarioPerfil.getIdUsuarioPerfil());
        }
        return usuarioPerfil;
    }

    public static boolean isUsuarioAtualResponsavel() {
        return getUsuarioPerfilAtual().getResponsavelLocalizacao();
    }

    private static UsuarioLoginDAO getUsuarioLoginDAO() {
        return (UsuarioLoginDAO) Component.getInstance(UsuarioLoginDAO.NAME);
    }

    private static UsuarioPerfilDAO getUsuarioPerfilDAO() {
        return (UsuarioPerfilDAO) Component.getInstance(UsuarioPerfilDAO.NAME);
    }

    /**
     * Atalho para a localização atual
     * 
     * @return localização atual do usuário logado
     */
    public static Localizacao getLocalizacaoAtual() {
        UsuarioPerfil usuarioPerfilAtual = getUsuarioPerfilAtual();
        if (usuarioPerfilAtual != null) {
            return usuarioPerfilAtual.getLocalizacao();
        }
        return null;
    }

    public static Papel getPapelAtual() {
        UsuarioPerfil usuarioPerfilAtual = getUsuarioPerfilAtual();
        if (usuarioPerfilAtual != null) {
            return usuarioPerfilAtual.getPerfilTemplate().getPapel();
        }
        return null;
    }

    /**
     * Atalho para o usuario logado
     * 
     * @return usuário logado
     */
    public static UsuarioLogin getUsuarioLogado() {
        UsuarioLogin usuario = (UsuarioLogin) Contexts.getSessionContext().get("usuarioLogado");
        if (usuario == null) {
            return null;
        }
        return getUsuarioLoginDAO().find(usuario.getIdUsuarioLogin());
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    @SuppressWarnings(UNCHECKED)
    public List<SelectItem> getUsuarioPerfilListItems() {
        List<SelectItem> list = (List<SelectItem>) Contexts.getSessionContext().get(USUARIO_PERFIL_LIST);
        return list;
    }

    public void setUsuarioPerfilAtualCombo(Integer id) throws LoginException {
        setUsuarioPerfilAtual(getUsuarioPerfilDAO().find(id));
    }

    public Integer getUsuarioPerfilAtualCombo() {
        return getUsuarioPerfilAtual().getIdUsuarioPerfil();
    }
    
    public boolean isUsuarioLogadoInMonocratica() {
        return getUsuarioPerfilAtual().getLocalizacao().isDecisoraMonocratica();
    }
    
    public boolean isUsuarioLogadoInColegiada() {
        return getUsuarioPerfilAtual().getLocalizacao().isDecisoraColegiada();
    }
    
    public boolean isUsuarioLogandoInMonocraticaAndColegiada() {
        return isUsuarioLogadoInMonocratica() && getColegiadaParaMonocraticaLogada() != null;
    }
    
    public UnidadeDecisoraMonocratica getMonocraticaLogada() {
        if (isUsuarioLogadoInMonocratica()) {
            return getUsuarioPerfilAtual().getLocalizacao().getUnidadeDecisoraMonocratica().get(0);
        } else {
            return null;
        }
    }
    
    public UnidadeDecisoraColegiada getColegiadaLogada() {
        if (isUsuarioLogadoInColegiada()) {
            return getUsuarioPerfilAtual().getLocalizacao().getUnidadeDecisoraColegiada().get(0);
        } else if (isUsuarioLogadoInMonocratica()) {
            return getColegiadaParaMonocraticaLogada();
        } else {
            return null;
        }
    }
    
    public void setColegiadaParaMonocraticaLogada(UnidadeDecisoraColegiada decisoraColegiada) {
        Contexts.getSessionContext().set(COLEGIADA_DA_MONOCRATICA_LOGADA, decisoraColegiada);
    }
    
    public UnidadeDecisoraColegiada getColegiadaParaMonocraticaLogada() {
        return (UnidadeDecisoraColegiada) Contexts.getSessionContext().get(COLEGIADA_DA_MONOCRATICA_LOGADA);
    }
     
    public List<UnidadeDecisoraColegiada> getColegiadasParaMonocraticaLogada() {
        if (isUsuarioLogadoInMonocratica()) {
            return getMonocraticaLogada().getUnidadeDecisoraColegiadaList();
        } else {
            return new ArrayList<>();
        }
    }
    
    public String getUsuarioPerfilAtualSingle(){
    	return getUsuarioPerfilAtual().toString();
    }

    private static AuthenticatorService getAuthenticatorService() {
        return (AuthenticatorService) Component.getInstance(AuthenticatorService.NAME);
    }

    public static void loginUsuarioExterno() {
        Identity identity = Identity.instance();
        Credentials credentials = identity.getCredentials();
        credentials.setUsername(ParametroUtil.LOGIN_USUARIO_EXTERNO);
        credentials.setPassword("usuarioexterno");
        identity.quietLogin();
        identity.login();
        UsuarioLoginManager usuarioLoginManager = (UsuarioLoginManager) Component.getInstance(UsuarioLoginManager.NAME);
        Contexts.getSessionContext().set(USUARIO_LOGADO, usuarioLoginManager.getUsuarioLoginByLogin(credentials.getUsername()));
    }

    public boolean isUsuarioExterno() {
        if (Identity.instance().isLoggedIn()) {
            return getUsuarioLogado().getLogin().equals(ParametroUtil.getLoginUsuarioExterno());
        }
        return false;
    }
}
