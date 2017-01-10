package br.com.infox.epp.access.service;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.security.Principal;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.security.auth.login.LoginException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.SimplePrincipal;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.util.Strings;

import br.com.infox.certificado.Certificado;
import br.com.infox.certificado.CertificadoDadosPessoaFisica;
import br.com.infox.certificado.CertificadoFactory;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.dao.UsuarioPerfilDAO;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.BloqueioUsuarioManager;
import br.com.infox.epp.access.manager.CertificateManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.type.UsuarioEnum;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.RedirectToLoginApplicationException;

@Stateless
@AutoCreate
@Scope(ScopeType.STATELESS)
@Name(AuthenticatorService.NAME)
public class AuthenticatorService {

    public static final String LOGIN_ERROR_SEM_PESSOA_FISICA = "login.error.semPessoaFisica";
    public static final String LOGIN_ERROR_CPF_NAO_ENCONTRADO = "login.error.semCPF";
    public static final String LOGIN_ERROR_TERMO_ADESAO_FAILED = "login.termoAdesao.failed";
    public static final String LOGIN_ERROR_USUARIO_SEM_PERFIL = "login.error.semPerfil";
    public static final String LOGIN_ERROR_LOGIN_CERTIFICADO_DESABILITADO = "login.error.loginCertificadoDesabilitado";
    public static final String LOGIN_ERROR_LOGIN_COM_SENHA_DESABILITADO = "login.error.loginComSenhaDesabilitado";
    public static final String CERTIFICATE_ERROR_EXPIRED = "certificate.error.expired";
    public static final String LOGIN_ERROR_USUARIO_PROVISORIO_EXPIRADO = "login.error.expirado";
    public static final String LOGIN_ERROR_USUARIO_BLOQUEADO = "login.error.bloqueado";
    public static final String LOGIN_ERROR_USUARIO_INATIVO = "login.error.inativo";
    public static final String LOGIN_ERROR_LOGIN_DESABILITADO = "login.error.loginDesabilitado";
    public static final String LOGIN_ERROR_USUARIO_INEXISTENTE = "login.error.inexistente";
    private static final String SEAM_SECURITY_CREDENTIALS = "org.jboss.seam.security.credentials";
    private static final String CHECK_VALIDADE_CERTIFICADO = "CertificateAuthenticator.checkValidadeCertificado(Certificado)";
    public static final String NAME = "authenticatorService";

    @Inject
    private UsuarioLoginManager usuarioLoginManager;
    @Inject
    private BloqueioUsuarioManager bloqueioUsuarioManager;
    @Inject
    private PessoaFisicaManager pessoaFisicaManager;
    @Inject
    private UsuarioPerfilDAO usuarioPerfilDAO;
    @Inject
    private InfoxMessages infoxMessages;

    public static final String CERTIFICATE_ERROR_UNKNOWN = "certificate.error.unknown";

    private static final LogProvider LOG = Logging.getLogProvider(AuthenticatorService.class);

    public static final String PAPEIS_USUARIO_LOGADO = "papeisUsuarioLogado";
    public static final String USUARIO_LOGADO = "usuarioLogado";
    public static final String USUARIO_PERFIL_LIST = "usuarioPerfilList";

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
     * 
     * @param usuario
     */
    public void setUsuarioLogadoSessao(UsuarioLogin usuario) {
        Contexts.getSessionContext().set(USUARIO_LOGADO, usuario);
        List<SelectItem> usuarioPerfilList = new ArrayList<>();
        for (UsuarioPerfil usuarioPerfil : usuarioPerfilDAO.listByUsuarioLogin(usuario)) {
            usuarioPerfilList.add(new SelectItem(usuarioPerfil.getIdUsuarioPerfil(), usuarioPerfil.toString()));
        }
        Contexts.getSessionContext().set(USUARIO_PERFIL_LIST, usuarioPerfilList);
    }

    public UsuarioLogin getUsuarioByLogin(String login) {
        return usuarioLoginManager.getUsuarioLoginByLogin(login);
    }

    @SuppressWarnings(UNCHECKED)
    public void removeRolesAntigas() {
        Set<String> roleSet = (Set<String>) Contexts.getSessionContext().get(PAPEIS_USUARIO_LOGADO);
        if (roleSet != null) {
            for (String r : roleSet) {
                Identity.instance().removeRole(r);
            }
        }
    }

    public void logDaBuscaDasRoles(UsuarioPerfil usuarioPerfil) {
        LOG.warn("Obter role do Perfil: " + usuarioPerfil);
        LOG.warn("Obter role do papel: " + usuarioPerfil.getPerfilTemplate().getPapel());
    }

    public void addRolesAtuais(Set<String> roleSet) {
        for (String role : roleSet) {
            Identity.instance().addRole(role);
        }
    }

    // TODO refazer essa busca pelo PerfilAtual
    public UsuarioPerfil obterPerfilAtual(UsuarioLogin usuario) throws LoginException {
        List<UsuarioPerfil> usuarioPerfilList = new ArrayList<>(usuarioPerfilDAO.listByUsuarioLogin(usuario));
        if (usuarioPerfilList.size() > 0) {
            UsuarioPerfil usuarioPerfil = usuarioPerfilList.get(0);
            return usuarioPerfilDAO.getReference(usuarioPerfil.getIdUsuarioPerfil());
        }
        
        throw new LoginException(String.format(infoxMessages.get(LOGIN_ERROR_USUARIO_SEM_PERFIL), usuario));
    }

    public void signatureAuthentication(UsuarioLogin usuario, String signature, String certChain, boolean termoAdesao)
            throws CertificadoException, LoginException, CertificateException, DAOException {
        boolean loggedIn = login(usuario.getLogin());
        if (loggedIn) {
            PessoaFisica pessoaFisica = usuario.getPessoaFisica();
            if (pessoaFisica.getCertChain() == null) {
                pessoaFisica.setCertChain(certChain);
                pessoaFisicaManager.merge(pessoaFisica);
                pessoaFisicaManager.flush();
            }
            if (signature == null && termoAdesao) {
                throw new RedirectToLoginApplicationException(infoxMessages.get(LOGIN_ERROR_TERMO_ADESAO_FAILED));
            }
        }
    }

    public UsuarioLogin getUsuarioLoginFromCertChain(String certChain) throws CertificadoException, LoginException, CertificateException {
    	if (Strings.isEmpty(certChain)) {
    		throw new CertificadoException("Sem certificado");
    	}
        final Certificado c = CertificadoFactory.createCertificado(certChain);
        checkValidadeCertificado(c);
        String cpf;
        if(((CertificadoDadosPessoaFisica)c).getCPF()  != null && !((CertificadoDadosPessoaFisica)c).getCPF().isEmpty() ){
			cpf = new StringBuilder(((CertificadoDadosPessoaFisica) c).getCPF()).insert(9, '-').insert(6, '.')
	                .insert(3, '.').toString();
        }else{
        	throw new LoginException(infoxMessages.get(LOGIN_ERROR_CPF_NAO_ENCONTRADO));
        }
        return checkValidadeLoginCertificado(cpf);
    }

    private UsuarioLogin checkValidadeLoginCertificado(final String cpf) throws LoginException {
        final PessoaFisica pessoaFisica = pessoaFisicaManager.getByCpf(cpf);
        if (pessoaFisica == null) {
            throw new LoginException(infoxMessages.get(LOGIN_ERROR_USUARIO_INEXISTENTE));
        }
        final UsuarioLogin usuarioLogin;
        usuarioLogin = usuarioLoginManager.getUsuarioLoginByPessoaFisica(pessoaFisica);
        checkValidadeUsuarioLogin(usuarioLogin, UsuarioEnum.C);
        return usuarioLogin;
    }

    public void checkValidadeUsuarioLogin(final UsuarioLogin usuarioLogin, UsuarioEnum tipoLoginValido) throws LoginException {
        if (usuarioLogin == null) {
            throw new LoginException(infoxMessages.get(LOGIN_ERROR_USUARIO_INEXISTENTE));
        }
        
        validaMeioLogin(usuarioLogin.getTipoUsuario(), tipoLoginValido);
        
        if (usuarioLogin.isUsuarioSistema()) {
            throw new LoginException(infoxMessages.get(LOGIN_ERROR_LOGIN_DESABILITADO));
        }
        if (!usuarioLogin.getAtivo()) {
            throw new LoginException(infoxMessages.get(LOGIN_ERROR_USUARIO_INATIVO));
        }
        if (usuarioLogin.getBloqueio()) {
            if (bloqueioUsuarioManager.liberarUsuarioBloqueado(usuarioLogin)) {
                bloqueioUsuarioManager.desfazerBloqueioUsuario(usuarioLogin);
            } else {
                throw new LoginException(infoxMessages.get(LOGIN_ERROR_USUARIO_BLOQUEADO));
            }
        }
        if (usuarioLogin.getProvisorio() && usuarioLoginManager.usuarioExpirou(usuarioLogin)) {
            throw new LoginException(infoxMessages.get(LOGIN_ERROR_USUARIO_PROVISORIO_EXPIRADO));
        }
    }

    private void validaMeioLogin(final UsuarioEnum tipoLoginUsuario, UsuarioEnum tipoLoginValido) throws LoginException {
        if (UsuarioEnum.S.equals(tipoLoginUsuario))
            throw new LoginException(infoxMessages.get(LOGIN_ERROR_LOGIN_DESABILITADO));
        
        if (!UsuarioEnum.H.equals(tipoLoginUsuario) && !tipoLoginUsuario.equals(tipoLoginValido)) {
            if (UsuarioEnum.P.equals(tipoLoginValido))
                throw new LoginException(infoxMessages.get(LOGIN_ERROR_LOGIN_COM_SENHA_DESABILITADO));
            else if (UsuarioEnum.C.equals(tipoLoginValido))
                throw new LoginException(infoxMessages.get(LOGIN_ERROR_LOGIN_CERTIFICADO_DESABILITADO));
            
        }
    }

    /**
     * @deprecated Método duplicado, utilizar {@link AuthenticatorService#checkValidadeUsuarioLogin(UsuarioLogin, UsuarioEnum)}
     */
    @Deprecated
    public void validarUsuario(UsuarioLogin usuario) throws LoginException, DAOException {
        checkValidadeUsuarioLogin(usuario, UsuarioEnum.P);
    }

    public void loginWithoutPassword(UsuarioLogin usuarioLogin){
    	loginWithoutPassword(usuarioLogin.getLogin());
    }
    public void loginWithoutPassword(String login){
		if (login(login)) {
			Events events = Events.instance();
			events.raiseEvent(Identity.EVENT_LOGIN_SUCCESSFUL, new Object[1]);
			events.raiseEvent(Identity.EVENT_POST_AUTHENTICATE, new Object[1]);
		}
    }
    
    protected boolean login(final String login) {
        IdentityManager identityManager = IdentityManager.instance();
        boolean userExists = identityManager.getIdentityStore().userExists(login);
        if (userExists) {
            Principal principal = new SimplePrincipal(login);
            Identity identity = Identity.instance();
            identity.acceptExternallyAuthenticatedPrincipal(principal);
            Credentials credentials = (Credentials) Component.getInstance(SEAM_SECURITY_CREDENTIALS);
            credentials.clear();
            credentials.setUsername(login);
        }
        return userExists;
    }

    private void checkValidadeCertificado(final Certificado c) throws LoginException, CertificateException {
        try {
            CertificateManager.instance().verificaCertificado(c.getCertChain());
        } catch (CertificateException e) {
            LOG.error(CHECK_VALIDADE_CERTIFICADO, e);
            if (ParametroUtil.isValidaAssinatura()) {
                throw e;
            }
        }
    }
}
