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
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Events;
import org.jboss.seam.security.Identity;

import br.com.infox.certificado.CertificateSignatures;
import br.com.infox.certificado.bean.CertificateSignatureBundleBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleStatus;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.RedirectToLoginApplicationException;

@Name(CertificateAuthenticator.NAME)
@Scope(ScopeType.CONVERSATION)
@Transactional
public class CertificateAuthenticator implements Serializable {

    private static final long serialVersionUID = 6825659622529568148L;
    private static final String AUTHENTICATE = "certificateAuthenticator.authenticate()";
    private static final LogProvider LOG = Logging.getLogProvider(CertificateAuthenticator.class);
    public static final String NAME = "certificateAuthenticator";
    private boolean certificateLogin = false;
    private String token;

    @In
    private UsuarioLoginManager usuarioLoginManager;
    @In
    private AuthenticatorService authenticatorService;
    @In
    private CertificateSignatures certificateSignatures;
    @In
    private InfoxMessages infoxMessages;

    public void authenticate() {
        try {
            CertificateSignatureBundleBean bundle = getSignatureBundle();
            String certChain = bundle.getSignatureBeanList().get(0).getCertChain();
            UsuarioLogin usuarioLogin = authenticatorService.getUsuarioLoginFromCertChain(certChain);
            authenticatorService.signatureAuthentication(usuarioLogin, null, certChain, false);
            final Events events = Events.instance();
            events.raiseEvent(Identity.EVENT_LOGIN_SUCCESSFUL, new Object[1]);
            events.raiseEvent(Identity.EVENT_POST_AUTHENTICATE, new Object[1]);
        } catch (final CertificateExpiredException e) {
            LOG.error(AUTHENTICATE, e);
            throw new RedirectToLoginApplicationException(infoxMessages.get(CERTIFICATE_ERROR_EXPIRED), e);
        } catch (final CertificateException e) {
            LOG.error(AUTHENTICATE, e);
            throw new RedirectToLoginApplicationException(
                    format(infoxMessages.get(AuthenticatorService.CERTIFICATE_ERROR_UNKNOWN), e.getMessage()), e);
        } catch (CertificadoException | LoginException | DAOException e) {
            LOG.error(AUTHENTICATE, e);
            throw new RedirectToLoginApplicationException(e.getMessage(), e);
        }

    }

    private CertificateSignatureBundleBean getSignatureBundle() throws CertificadoException {
        CertificateSignatureBundleBean bundle = certificateSignatures.get(token);
        if (bundle == null || bundle.getStatus() != CertificateSignatureBundleStatus.SUCCESS) {
            throw new CertificadoException(infoxMessages.get("login.sign.error") + bundle);
        }
        return bundle;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isCertificateLogin() {
        return certificateLogin || ParametroUtil.isLoginComAssinatura();
    }

    public void setCertificateLogin(final boolean certificateLogin) {
        this.certificateLogin = certificateLogin;
    }

}
