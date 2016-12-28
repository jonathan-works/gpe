package br.com.infox.epp.access.api;

import static br.com.infox.epp.access.service.AuthenticatorService.CERTIFICATE_ERROR_EXPIRED;
import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Events;
import org.jboss.seam.security.Identity;

import br.com.infox.assinador.rest.api.StatusToken;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.CollectionUtil;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.DadosAssinatura;
import br.com.infox.epp.assinador.assinavel.AssinavelGenericoProvider;
import br.com.infox.epp.assinador.assinavel.AssinavelProvider;
import br.com.infox.epp.assinador.view.AssinaturaCallback;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.system.util.ParametroUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.RedirectToLoginApplicationException;

@Name(CertificateAuthenticator.NAME)
@Scope(ScopeType.CONVERSATION)
@Transactional
@ContextDependency
public class CertificateAuthenticator implements Serializable, AssinaturaCallback {

    private static final long serialVersionUID = 6825659622529568148L;
    private static final String AUTHENTICATE = "certificateAuthenticator.authenticate()";
    private static final LogProvider LOG = Logging.getLogProvider(CertificateAuthenticator.class);
    public static final String NAME = "certificateAuthenticator";
    private boolean certificateLogin = false;

    @Inject
    private AuthenticatorService authenticatorService;
    @Inject
    private InfoxMessages infoxMessages;
    @Inject
    private AssinadorService assinadorService;
    private AssinavelProvider assinavelProvider;
    
    @Override
    @ExceptionHandled
    public void onSuccess(List<DadosAssinatura> dadosAssinaturaList) {
        try {
            if (!CollectionUtil.isEmpty(dadosAssinaturaList)){
                DadosAssinatura dadosAssinatura = dadosAssinaturaList.get(0);
                if (dadosAssinatura == null || dadosAssinatura.getStatus() != StatusToken.SUCESSO) {
                    throw new CertificadoException(infoxMessages.get("login.sign.error") + dadosAssinatura);
                }
                    
                String certChain = dadosAssinatura.getCertChainBase64();
                UsuarioLogin usuarioLogin = authenticatorService.getUsuarioLoginFromCertChain(certChain);
                
                assinadorService.validarAssinaturas(dadosAssinaturaList, usuarioLogin.getPessoaFisica());
                authenticatorService.signatureAuthentication(usuarioLogin, null, certChain, false);
                Events events = Events.instance();
                events.raiseEvent(Identity.EVENT_LOGIN_SUCCESSFUL, new Object[1]);
                events.raiseEvent(Identity.EVENT_POST_AUTHENTICATE, new Object[1]);
            } else {
                throw new CertificadoException(infoxMessages.get("login.sign.error"));
            }
        } catch (CertificateExpiredException e) {
            LOG.error(AUTHENTICATE, e);
            throw new RedirectToLoginApplicationException(infoxMessages.get(CERTIFICATE_ERROR_EXPIRED), e);
        } catch (CertificateException e) {
            LOG.error(AUTHENTICATE, e);
            throw new RedirectToLoginApplicationException(
                    format(infoxMessages.get(AuthenticatorService.CERTIFICATE_ERROR_UNKNOWN), e.getMessage()), e);
        } catch (CertificadoException | LoginException | DAOException | AssinaturaException e) {
            LOG.error(AUTHENTICATE, e);
            throw new RedirectToLoginApplicationException(e.getMessage(), e);
        }
    }
    @Override
    @ExceptionHandled
    public void onFail(StatusToken statusToken, List<DadosAssinatura> dadosAssinatura) {
        try {
            throw new CertificadoException(infoxMessages.get("login.sign.error"));
        } catch (CertificadoException | DAOException e) {
            LOG.error(AUTHENTICATE, e);
            throw new RedirectToLoginApplicationException(e.getMessage(), e);
        }
    }
    
    public AssinavelProvider getAssinavelProvider(){
        if (this.assinavelProvider == null){
            this.assinavelProvider = new AssinavelGenericoProvider(UUID.randomUUID().toString());
        }
        return this.assinavelProvider;
    }
    
}
