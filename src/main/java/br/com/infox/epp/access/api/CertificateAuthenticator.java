package br.com.infox.epp.access.api;

import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.util.Date;

import javax.security.auth.login.LoginException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.Messages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.SimplePrincipal;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.util.Strings;

import br.com.infox.certificado.Certificado;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.CertificateManager;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.infox.seam.exception.RedirectToLoginApplicationException;

@Name(CertificateAuthenticator.NAME)
public class CertificateAuthenticator implements Serializable {
    private static final String CERTIFICATE_ERROR_UNKNOWN = "certificate.error.unknown";
    private static final String CERTIFICATE_ERROR_USUARIO_LOGIN_PROVISORIO_EXPIRADO = "certificate.error.usuarioLoginProvisorioExpirado";
    private static final String CERTIFICATE_ERROR_USUARIO_LOGIN_BLOQUEADO = "certificate.error.usuarioLoginBloqueado";
    private static final String CERTIFICATE_ERROR_USUARIO_LOGIN_INATIVO = "certificate.error.usuarioLoginInativo";
    private static final String CERTIFICATE_ERROR_TIPO_USUARIO_SISTEMA = "certificate.error.tipoUsuarioSistema";
    private static final String CERTIFICATE_ERROR_SEM_USUARIO_LOGIN = "certificate.error.semUsuarioLogin";
    private static final String CERTIFICATE_ERROR_SEM_PESSOA_FISICA = "certificate.error.semPessoaFisica";
    private static final String CERTIFICATE_ERROR_EXPIRED = "certificate.error.expired";
    private static final String CERTIFICATE_INVALID = "certificate.error.invalid";
    private static final String SEAM_SECURITY_CREDENTIALS = "org.jboss.seam.security.credentials";
    private static final String CHECK_VALIDADE_CERTIFICADO = "CertificateAuthenticator.checkValidadeCertificado(Certificado)";
    private static final long serialVersionUID = 6825659622529568148L;
    private static final String AUTHENTICATE = "certificateAuthenticator.authenticate()";
    private static final LogProvider LOG = Logging.getLogProvider(CertificateAuthenticator.class);
    public static final String NAME = "certificateAuthenticator";
    private String assinatura;
    private String certChain;
    private boolean certificateLogin = false;

    @In
    private UsuarioLoginManager usuarioLoginManager;
    @In
    private PessoaFisicaManager pessoaFisicaManager;
    @In
    private CertificateManager certificateManager;

    public void authenticate() {
        String cpf = null;

        try {
            final Certificado c = new Certificado(certChain);
            cpf = extractCpf(c);
            checkValidadeCertificado(c);

            final UsuarioLogin usuario = checkValidadeUsuarioLogin(cpf);

            final boolean loggedIn = login(usuario.getLogin());
            if (loggedIn) {
                final PessoaFisica pessoaFisica = usuario.getPessoaFisica();
                if (pessoaFisica.getCertChain() == null) {
                    persistCertChain(usuario.getPessoaFisica());
                }
                raiseLoginEvents();
            }
        } catch (final CertificateExpiredException e) {
            LOG.error(AUTHENTICATE, e);
            throw new RedirectToLoginApplicationException(Messages.instance().get(CERTIFICATE_ERROR_EXPIRED), e);
        } catch (final CertificateException e) {
            LOG.error(AUTHENTICATE, e);
            throw new RedirectToLoginApplicationException(format(Messages.instance().get(CERTIFICATE_ERROR_UNKNOWN), e.getCause().toString()), e);
        } catch (CertificadoException | LoginException | DAOException e) {
            LOG.error(AUTHENTICATE, e);
            throw new RedirectToLoginApplicationException(e.getMessage(), e);
        }

    }

    private UsuarioLogin checkValidadeUsuarioLogin(final String cpf) throws LoginException {
        final PessoaFisica pessoaFisica = pessoaFisicaManager.getByCpf(cpf);
        if (pessoaFisica == null) {
            throw new LoginException(Messages.instance().get(CERTIFICATE_ERROR_SEM_PESSOA_FISICA));
        }
        final UsuarioLogin usuarioLogin;
        usuarioLogin = usuarioLoginManager.getUsuarioLoginByPessoaFisica(pessoaFisica);
        if (usuarioLogin == null) {
            throw new LoginException(Messages.instance().get(CERTIFICATE_ERROR_SEM_USUARIO_LOGIN));
        }
        if (!usuarioLogin.isHumano()) {
            throw new LoginException(Messages.instance().get(CERTIFICATE_ERROR_TIPO_USUARIO_SISTEMA));
        }
        if (!usuarioLogin.getAtivo()) {
            throw new LoginException(Messages.instance().get(CERTIFICATE_ERROR_USUARIO_LOGIN_INATIVO));
        }
        if (usuarioLogin.getBloqueio()) {
            throw new LoginException(Messages.instance().get(CERTIFICATE_ERROR_USUARIO_LOGIN_BLOQUEADO));
        }
        if (usuarioLogin.getProvisorio()
                && new Date().after(usuarioLogin.getDataExpiracao())) {
            throw new LoginException(Messages.instance().get(CERTIFICATE_ERROR_USUARIO_LOGIN_PROVISORIO_EXPIRADO));
        }
        return usuarioLogin;
    }

    private void raiseLoginEvents() {
        final Events events = Events.instance();
        events.raiseEvent(Identity.EVENT_LOGIN_SUCCESSFUL, new Object[1]);
        events.raiseEvent(Identity.EVENT_POST_AUTHENTICATE, new Object[1]);
    }

    private void persistCertChain(final PessoaFisica pessoaFisica) throws DAOException {
        pessoaFisica.setCertChain(certChain);
        pessoaFisicaManager.merge(pessoaFisica);
    }

    private boolean login(final String login) {
        final IdentityManager identityManager = IdentityManager.instance();
        final boolean userExists = identityManager.getIdentityStore().userExists(login);
        if (userExists) {
            final Principal principal = new SimplePrincipal(login);
            final Identity identity = Identity.instance();
            identity.acceptExternallyAuthenticatedPrincipal(principal);
            final Credentials credentials = (Credentials) Component.getInstance(SEAM_SECURITY_CREDENTIALS);
            credentials.clear();
            credentials.setUsername(login);
        }
        return userExists;
    }

    private void checkValidadeCertificado(final Certificado c) throws LoginException, CertificateException {
        try {
            certificateManager.verificaCertificado(c.getCertChain());
        } catch (final CertificateExpiredException e) {
            LOG.error(CHECK_VALIDADE_CERTIFICADO, e);
            if (ParametroUtil.isProducao()) {
                throw e;
            }
        }
    }

    private String extractCpf(final Certificado c) throws LoginException {
        String cpf;
        final String[] split = c.getCn().split(":");
        if (split.length < 2) {
            throw new LoginException(Messages.instance().get(CERTIFICATE_INVALID));
        }
        cpf = split[1];
        if (Strings.isEmpty(cpf)) {
            throw new LoginException(Messages.instance().get(CERTIFICATE_INVALID));
        }
        cpf = new StringBuilder(cpf).insert(9, '-').insert(6, '.').insert(3, '.').toString();
        return cpf;
    }

    public String getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(final String assinatura) {
        this.assinatura = assinatura;
    }

    public String getCertChain() {
        return certChain;
    }

    public void setCertChain(final String certChain) {
        this.certChain = certChain;
    }

    public boolean isCertificateLogin() {
        return certificateLogin || ParametroUtil.isLoginComAssinatura();
    }

    public void setCertificateLogin(final boolean certificateLogin) {
        this.certificateLogin = certificateLogin;
    }

}
