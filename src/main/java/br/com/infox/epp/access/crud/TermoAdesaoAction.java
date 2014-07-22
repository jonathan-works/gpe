package br.com.infox.epp.access.crud;

import static br.com.infox.epp.access.service.AuthenticatorService.CERTIFICATE_ERROR_EXPIRED;
import static br.com.infox.epp.access.service.AuthenticatorService.CERTIFICATE_ERROR_UNKNOWN;
import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.Messages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoBinManager;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.seam.exception.RedirectToLoginApplicationException;

@Scope(ScopeType.CONVERSATION)
@Name(value = TermoAdesaoAction.NAME)
public class TermoAdesaoAction implements Serializable {
    private static final String METHOD_ASSINAR_TERMO_ADESAO = "termoAdesaoAction.assinarTermoAdesao(String,String)";
    private static final String PARAMETRO_TERMO_ADESAO = "termoAdesao";
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging
            .getLogProvider(TermoAdesaoAction.class);
    public static final String NAME = "termoAdesaoAction";
    public static final String PANEL_NAME = "termoAdesaoPanel";
    public static final String TERMO_ADESAO_REQ = "termoAdesaoRequired";

    private String signature;
    private String certChain;
    private String termoAdesao;
    private String tituloTermoAdesao;

    @In
    private ParametroManager parametroManager;
    @In
    private ModeloDocumentoManager modeloDocumentoManager;
    @In
    private AuthenticatorService authenticatorService;
    @In
    private ProcessoDocumentoBinManager processoDocumentoBinManager;
    @In
    private AssinaturaDocumentoService assinaturaDocumentoService;

    public void assinarTermoAdesao() {
        try {
            UsuarioLogin usuarioLogin = authenticatorService.getUsuarioLoginFromCertChain(certChain);
            authenticatorService.signatureAuthentication(usuarioLogin, signature, certChain, true);
            ProcessoDocumentoBin bin = processoDocumentoBinManager.createProcessoDocumentoBin(tituloTermoAdesao, termoAdesao);

            final List<UsuarioPerfil> perfilAtivoList = usuarioLogin.getUsuarioPerfilAtivoList();
            if (perfilAtivoList != null) {
                UsuarioPerfil perfil = null;
                for (UsuarioPerfil usuarioPerfil : perfilAtivoList) {
                    if ((perfil = usuarioPerfil).getPerfil().getPapel().getTermoAdesao()) {
                        break;
                    }
                }
                assinaturaDocumentoService.assinarDocumento(bin, perfil, certChain, signature);
            }
            processoDocumentoBinManager.flush();
        } catch (final CertificateExpiredException e) {
            LOG.error(METHOD_ASSINAR_TERMO_ADESAO, e);
            throw new RedirectToLoginApplicationException(Messages.instance().get(CERTIFICATE_ERROR_EXPIRED), e);
        } catch (final CertificateException e) {
            LOG.error(METHOD_ASSINAR_TERMO_ADESAO, e);
            throw new RedirectToLoginApplicationException(format(Messages.instance().get(CERTIFICATE_ERROR_UNKNOWN),e.getMessage()), e);
        } catch (CertificadoException | LoginException | DAOException e) {
            LOG.error(METHOD_ASSINAR_TERMO_ADESAO, e);
            throw new RedirectToLoginApplicationException(e.getMessage(), e);
        } catch (AssinaturaException e) {
            LOG.error(METHOD_ASSINAR_TERMO_ADESAO, e);
        }
    }

    public String getTermoAdesao() {
        if (termoAdesao == null) {
            Parametro parametro = parametroManager.getParametro(PARAMETRO_TERMO_ADESAO);
            if (parametro != null) {
                ModeloDocumento modeloDocumento = modeloDocumentoManager.getModeloDocumentoByTitulo(tituloTermoAdesao=parametro.getValorVariavel());
                termoAdesao = modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento);
            }
            if (termoAdesao == null) {
                termoAdesao = "<div><p>TERMO DE ADESÃO</p></div>";
            }
        }
        return termoAdesao;
    }

    public String getTermoAdesaoPanelName() {
        return PANEL_NAME;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getCertChain() {
        return certChain;
    }

    public void setCertChain(String certChain) {
        this.certChain = certChain;
    }
}
