package br.com.infox.epp.access.api;

import java.security.Principal;
import java.security.cert.CertificateExpiredException;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.Messages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.SimplePrincipal;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.util.Strings;

import br.com.infox.certificado.Certificado;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.certificado.util.DigitalSignatureUtils;
import br.com.infox.core.exception.NoRedirectApplicationException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.system.util.ParametroUtil;

@Name(CertificateAuthenticator.NAME)
public class CertificateAuthenticator {
    private static final String AUTHENTICATE = "authenticate";
    private static final LogProvider LOG = Logging.getLogProvider(CertificateAuthenticator.class);
    public static final String NAME = "certificateAuthenticator";
    private String assinatura;
    private String certChain;
    private String certChainStringLog;
    private boolean certificateLogin=false;
    
    @In
    private UsuarioLoginManager usuarioLoginManager;
    @In
    private PessoaFisicaManager pessoaFisicaManager;

    public void authenticate() {
        String cpf = null;
        try {
            final Certificado c = new Certificado(certChain);
            cpf = extractCpf(c);
            checkValidadeCertificado(c);
            
            final PessoaFisica pessoaFisica = checkValidadePessoaFisica(cpf);
            final UsuarioLogin usuario = checkValidadeUsuarioLogin(pessoaFisica);
            final boolean loggedIn = login(usuario.getLogin());
            if (loggedIn) {
                if (pessoaFisica.getCertChain() == null) {
                    persistCertChain(pessoaFisica);    
                }
                raiseLoginEvents();
            }
        } catch (CertificadoException | LoginException | DAOException e) {
            LOG.error(AUTHENTICATE, e);
            throw new NoRedirectApplicationException(AUTHENTICATE, e);
        } catch (final CertificateExpiredException e) {
            if (ParametroUtil.isProducao()) {
                throw new NoRedirectApplicationException(Messages.instance().get("certificado.expired"), e);
            }
            LOG.error(AUTHENTICATE, e);
        }
    }

    private UsuarioLogin checkValidadeUsuarioLogin(final PessoaFisica pessoaFisica) throws LoginException {
        final UsuarioLogin usuarioLogin;
        usuarioLogin = usuarioLoginManager.getUsuarioLoginByPessoaFisica(pessoaFisica);
        if (usuarioLogin == null) {
            throw new LoginException("Não existe usuário relacionado a este cpf");                
        }
        if (!usuarioLogin.isHumano()) {
            throw new LoginException("usuario do sistema não pode fazer login com cartão");
        }
        if (!usuarioLogin.getAtivo()) {
            throw new LoginException("Usuário inativo");
        }
        if (usuarioLogin.getBloqueio()) {
            throw new LoginException("Usuário bloqueado");
        }
        if (usuarioLogin.getProvisorio() && new Date().after(usuarioLogin.getDataExpiracao())) {
            throw new LoginException("Tempo de uso do usuário expirado");
        }
        return usuarioLogin;
    }

    private PessoaFisica checkValidadePessoaFisica(String cpf) throws LoginException {
        final PessoaFisica pessoaFisica;
        pessoaFisica = pessoaFisicaManager.getByCpf(cpf);
        if (pessoaFisica == null) {
            throw new LoginException("Não existe pessoa com este cpf");
        }
        return pessoaFisica;
    }

    private void raiseLoginEvents() {
        final Events events = Events.instance();
        events.raiseEvent(Identity.EVENT_POST_AUTHENTICATE, new Object[1]);
        events.raiseEvent(Identity.EVENT_LOGIN_SUCCESSFUL, new Object[1]);
    }

    private void persistCertChain(final PessoaFisica pessoaFisica) throws DAOException {
        pessoaFisica.setCertChain(certChain);
        pessoaFisicaManager.merge(pessoaFisica);
    }

    private boolean login(final String login) {
        final IdentityManager identityManager = IdentityManager.instance();
        boolean userExists = identityManager.getIdentityStore().userExists(login);
        if (userExists) {
            final Principal principal = new SimplePrincipal(login);
            final Identity identity = Identity.instance();
            identity.acceptExternallyAuthenticatedPrincipal(principal);
//                final Credentials credentials = (Credentials) Component.getInstance("org.jboss.seam.security.credentials");
//                credentials.clear();
//                credentials.setUsername(login);
            final Credentials credentials = identity.getCredentials();
            credentials.clear();
            credentials.setUsername(login);
        }
        return userExists;
    }

    private void checkValidadeCertificado(final Certificado c) throws LoginException, CertificateExpiredException {
        //DigitalSignatureUtils.loadCertFromBase64String(certChain);
        Date now = new Date();
        if (now.before(c.getDataValidadeInicio()) || now.after(c.getDataValidadeFim())) {
            throw new CertificateExpiredException("certificado expired");
        }
    }
    
    private String extractCpf(final Certificado c) throws LoginException {
        String cpf;
        final String[] split = c.getCn().split(":");
        if (split.length < 2) {
            throw new LoginException("Invalid Certificate");
        }
        cpf = split[1];
        if (Strings.isEmpty(cpf)) {
            throw new LoginException("Invalid Certificate");
        }
        cpf = new StringBuilder(cpf).insert(9, '-').insert(6, '.').insert(3, '.').toString();
        return cpf;
    }

    public boolean isCertificateLogin() {
        return certificateLogin || ParametroUtil.isLoginComAssinatura();
    }

    public void setCertificateLogin(final boolean certificateLogin) {
        this.certificateLogin = certificateLogin;
    }

    public String getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(final String assinatura) {
        this.assinatura = assinatura;
    }

    public String getCertChainStringLog() {
        return certChainStringLog;
    }

    public void setCertChainStringLog(final String certChainStringLog) {
        this.certChainStringLog = certChainStringLog;
    }
}
