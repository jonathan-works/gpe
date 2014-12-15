package br.com.infox.epp.access.service;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;
import javax.security.auth.login.LoginException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.SimplePrincipal;
import org.jboss.seam.security.management.IdentityManager;

import br.com.infox.certificado.Certificado;
import br.com.infox.certificado.CertificadoDadosPessoaFisica;
import br.com.infox.certificado.CertificadoFactory;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.messages.Messages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.dao.UsuarioPerfilDAO;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.BloqueioUsuarioManager;
import br.com.infox.epp.access.manager.CertificateManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.processo.dao.ProcessoDAO;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException.Motivo;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.infox.seam.exception.RedirectToLoginApplicationException;

@Name(AuthenticatorService.NAME)
@AutoCreate
public class AuthenticatorService implements Serializable {
    public static final String CERTIFICATE_ERROR_EXPIRED = "certificate.error.expired";
    private static final String CERTIFICATE_ERROR_USUARIO_LOGIN_PROVISORIO_EXPIRADO = "certificate.error.usuarioLoginProvisorioExpirado";
    private static final String CERTIFICATE_ERROR_USUARIO_LOGIN_BLOQUEADO = "certificate.error.usuarioLoginBloqueado";
    private static final String CERTIFICATE_ERROR_USUARIO_LOGIN_INATIVO = "certificate.error.usuarioLoginInativo";
    private static final String CERTIFICATE_ERROR_TIPO_USUARIO_SISTEMA = "certificate.error.tipoUsuarioSistema";
    private static final String CERTIFICATE_ERROR_SEM_USUARIO_LOGIN = "certificate.error.semUsuarioLogin";
    private static final String CERTIFICATE_ERROR_SEM_PESSOA_FISICA = "certificate.error.semPessoaFisica";
    private static final String SEAM_SECURITY_CREDENTIALS = "org.jboss.seam.security.credentials";
    private static final String CHECK_VALIDADE_CERTIFICADO = "CertificateAuthenticator.checkValidadeCertificado(Certificado)";
    private static final long serialVersionUID = 1L;
    public static final String NAME = "authenticatorService";

    @In
    private UsuarioLoginManager usuarioLoginManager;
    @In
    private BloqueioUsuarioManager bloqueioUsuarioManager;
    @In
    private PessoaFisicaManager pessoaFisicaManager;
    @In
    private CertificateManager certificateManager;
    @In
    private UsuarioPerfilDAO usuarioPerfilDAO;
    @In
    private ProcessoDAO processoDAO;
    
    public static final String CERTIFICATE_ERROR_UNKNOWN = "certificate.error.unknown";

    private static final LogProvider LOG = Logging
            .getLogProvider(AuthenticatorService.class);

    public static final String PAPEIS_USUARIO_LOGADO = "papeisUsuarioLogado";
    public static final String USUARIO_LOGADO = "usuarioLogado";
    public static final String USUARIO_PERFIL_LIST = "usuarioPerfilList";

    public void autenticaManualmenteNoSeamSecurity(String login,
            IdentityManager identityManager) {
        Principal principal = new SimplePrincipal(login);
        Identity identity = Identity.instance();
        identity.acceptExternallyAuthenticatedPrincipal(principal);
        Credentials credentials = (Credentials) Component
                .getInstance(Credentials.class);
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
        for (UsuarioPerfil usuarioPerfil : usuario.getUsuarioPerfilAtivoList()){
        	usuarioPerfilList.add(new SelectItem(usuarioPerfil.getIdUsuarioPerfil(), usuarioPerfil.toString()));
        }
        Contexts.getSessionContext().set(USUARIO_PERFIL_LIST, usuarioPerfilList);
    }

    public void validarUsuario(UsuarioLogin usuario) throws LoginException,
            DAOException {
        if (usuario.getBloqueio()) {
            if (bloqueioUsuarioManager.liberarUsuarioBloqueado(usuario)) {
                bloqueioUsuarioManager.desfazerBloqueioUsuario(usuario);
            } else {
                throwUsuarioBloqueado(usuario);
            }
        } else if (usuario.getProvisorio()) {
            if (usuarioLoginManager.usuarioExpirou(usuario)) {
                usuarioLoginManager.inativarUsuario(usuario);
                throwUsuarioExpirou(usuario);
            }
        } else if (!usuario.getAtivo()) {
            throwUsuarioInativo(usuario);
        }
    }

    private void throwUsuarioExpirou(UsuarioLogin usuario)
            throws LoginException {
        throw new LoginException("O usuário " + usuario.getNomeUsuario()
                + " expirou. " + "Por favor, contate o adminstrador do sistema");
    }

    private void throwUsuarioInativo(UsuarioLogin usuario)
            throws LoginException {
        throw new LoginException("O usuário " + usuario.getNomeUsuario()
                + " não está ativo.\n");
    }

    private void throwUsuarioBloqueado(UsuarioLogin usuario)
            throws LoginException {
        throw new LoginException("O usuário " + usuario.getNomeUsuario()
                + " está bloqueado."
                + "Por favor, contate o adminstrador do sistema");
    }

    public UsuarioLogin getUsuarioByLogin(String login) {
        return usuarioLoginManager.getUsuarioLoginByLogin(login);
    }

    @SuppressWarnings(UNCHECKED)
    public void removeRolesAntigas() {
        Set<String> roleSet = (Set<String>) Contexts.getSessionContext().get(
                PAPEIS_USUARIO_LOGADO);
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
    public UsuarioPerfil obterPerfilAtual(UsuarioLogin usuario)
            throws LoginException {
        List<UsuarioPerfil> usuarioPerfilList = new ArrayList<>(
                usuario.getUsuarioPerfilList());
        if (usuarioPerfilList.size() > 0) {
            UsuarioPerfil usuarioPerfil = usuarioPerfilList.get(0);
            return usuarioPerfilDAO.getReference(usuarioPerfil
                    .getIdUsuarioPerfil());
        }
        throw new LoginException("O usuário " + usuario + " não possui Perfil");
    }

    public void signatureAuthentication(UsuarioLogin usuario, String signature, String certChain,boolean termoAdesao) throws CertificadoException, LoginException,CertificateException, DAOException {
        final boolean loggedIn = login(usuario.getLogin());
        if (loggedIn) {
            final PessoaFisica pessoaFisica = usuario.getPessoaFisica();
            if (pessoaFisica.getCertChain() == null) {
                pessoaFisica.setCertChain(certChain);
                pessoaFisicaManager.merge(pessoaFisica);
                pessoaFisicaManager.flush();
            } else {
            	if (!pessoaFisica.getCertChain().equals(certChain)) {
            		AssinaturaException ex = new AssinaturaException(Motivo.CERTIFICADO_USUARIO_DIFERENTE_CADASTRO);
            		throw new RedirectToLoginApplicationException(ex.getMessage());
            	}
            }
            if (signature == null && termoAdesao) {
                throw new RedirectToLoginApplicationException(Messages.resolveMessage("login.termoAdesao.failed"));
            }
        }
    }

    public UsuarioLogin getUsuarioLoginFromCertChain(String certChain) throws CertificadoException, LoginException, CertificateException{
        final Certificado c = CertificadoFactory.createCertificado(certChain);
        checkValidadeCertificado(c);
        String cpf = new StringBuilder(((CertificadoDadosPessoaFisica) c).getCPF()).insert(9, '-').insert(6, '.').insert(3, '.').toString();
        return checkValidadeUsuarioLogin(cpf);
    }
    
    private UsuarioLogin checkValidadeUsuarioLogin(final String cpf)
            throws LoginException {
        final PessoaFisica pessoaFisica = pessoaFisicaManager.getByCpf(cpf);
        if (pessoaFisica == null) {
            throw new LoginException(Messages.resolveMessage(
                    CERTIFICATE_ERROR_SEM_PESSOA_FISICA));
        }
        final UsuarioLogin usuarioLogin;
        usuarioLogin = usuarioLoginManager
                .getUsuarioLoginByPessoaFisica(pessoaFisica);
        if (usuarioLogin == null) {
            throw new LoginException(Messages.resolveMessage(
                    CERTIFICATE_ERROR_SEM_USUARIO_LOGIN));
        }
        if (!usuarioLogin.isHumano()) {
            throw new LoginException(Messages.resolveMessage(
                    CERTIFICATE_ERROR_TIPO_USUARIO_SISTEMA));
        }
        if (!usuarioLogin.getAtivo()) {
            throw new LoginException(Messages.resolveMessage(
                    CERTIFICATE_ERROR_USUARIO_LOGIN_INATIVO));
        }
        if (usuarioLogin.getBloqueio()) {
            throw new LoginException(Messages.resolveMessage(
                    CERTIFICATE_ERROR_USUARIO_LOGIN_BLOQUEADO));
        }
        if (usuarioLogin.getProvisorio()
                && new Date().after(usuarioLogin.getDataExpiracao())) {
            throw new LoginException(Messages.resolveMessage(
                    CERTIFICATE_ERROR_USUARIO_LOGIN_PROVISORIO_EXPIRADO));
        }
        return usuarioLogin;
    }

    private boolean login(final String login) {
        final IdentityManager identityManager = IdentityManager.instance();
        final boolean userExists = identityManager.getIdentityStore()
                .userExists(login);
        if (userExists) {
            final Principal principal = new SimplePrincipal(login);
            final Identity identity = Identity.instance();
            identity.acceptExternallyAuthenticatedPrincipal(principal);
            final Credentials credentials = (Credentials) Component
                    .getInstance(SEAM_SECURITY_CREDENTIALS);
            credentials.clear();
            credentials.setUsername(login);
        }
        return userExists;
    }

    private void checkValidadeCertificado(final Certificado c)
            throws LoginException, CertificateException {
        try {
            certificateManager.verificaCertificado(c.getCertChain());
        } catch (final CertificateExpiredException e) {
            LOG.error(CHECK_VALIDADE_CERTIFICADO, e);
            if (ParametroUtil.isProducao()) {
                throw e;
            }
        }
    }
}
