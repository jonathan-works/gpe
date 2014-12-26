package br.com.infox.epp.access.api;

import static br.com.infox.epp.access.service.AuthenticatorService.CERTIFICATE_ERROR_EXPIRED;
import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;

import javax.security.auth.login.LoginException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import org.jboss.seam.security.Identity;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.messages.Messages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.infox.seam.exception.RedirectToLoginApplicationException;

@Name(CertificateAuthenticator.NAME)
@Scope(ScopeType.CONVERSATION)
public class CertificateAuthenticator implements Serializable {
    private static final long serialVersionUID = 6825659622529568148L;
    private static final String AUTHENTICATE = "certificateAuthenticator.authenticate()";
    private static final LogProvider LOG = Logging
            .getLogProvider(CertificateAuthenticator.class);
    public static final String NAME = "certificateAuthenticator";
    private String assinatura;
    private String certChain;
    private boolean certificateLogin = false;

    @In
    private UsuarioLoginManager usuarioLoginManager;
    @In
    private AuthenticatorService authenticatorService;

    public void authenticate() {
        try {
            UsuarioLogin usuarioLogin = authenticatorService.getUsuarioLoginFromCertChain(certChain);
            authenticatorService.signatureAuthentication(usuarioLogin, null, certChain, false);
            final Events events = Events.instance();
            events.raiseEvent(Identity.EVENT_LOGIN_SUCCESSFUL, new Object[1]);
            events.raiseEvent(Identity.EVENT_POST_AUTHENTICATE, new Object[1]);
        } catch (final CertificateExpiredException e) {
            LOG.error(AUTHENTICATE, e);
            throw new RedirectToLoginApplicationException(Messages.resolveMessage(CERTIFICATE_ERROR_EXPIRED), e);
        } catch (final CertificateException e) {
            LOG.error(AUTHENTICATE, e);
            throw new RedirectToLoginApplicationException(format(
                    Messages.resolveMessage(
                            AuthenticatorService.CERTIFICATE_ERROR_UNKNOWN),
                    e.getMessage()), e);
        } catch (CertificadoException | LoginException | DAOException e) {
            LOG.error(AUTHENTICATE, e);
            throw new RedirectToLoginApplicationException(e.getMessage(), e);
        }

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
