package br.com.infox.epp.access.crud;

import java.io.Serializable;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.text.MessageFormat;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.messages.Messages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.manager.PessoaFisicaManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.seam.exception.RedirectToLoginApplicationException;

@Scope(ScopeType.CONVERSATION)
@Name(value = TermoAdesaoAction.NAME)
public class TermoAdesaoAction implements Serializable {
    private static final String TERMS_CONDITIONS_SIGN_SUCCESS = "termoAdesao.sign.success";
    private static final String METHOD_ASSINAR_TERMO_ADESAO = "termoAdesaoAction.assinarTermoAdesao(String,String)";
    private static final String PARAMETRO_TERMO_ADESAO = "termoAdesao";
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(TermoAdesaoAction.class);
    public static final String NAME = "termoAdesaoAction";
    public static final String PANEL_NAME = "termoAdesaoPanel";
    public static final String TERMO_ADESAO_REQ = "termoAdesaoRequired";

    private String signature;
    private String certChain;
    private String termoAdesao;
    private String tituloTermoAdesao;

    @In private ParametroManager parametroManager;
    @In private ModeloDocumentoManager modeloDocumentoManager;
    @In private AuthenticatorService authenticatorService;
    @In private DocumentoBinManager documentoBinManager;
    @In private AssinaturaDocumentoService assinaturaDocumentoService;
    @In private PessoaFisicaManager pessoaFisicaManager;

    public String assinarTermoAdesao(String certChain, String signature) {
        try {
            UsuarioLogin usuarioLogin = authenticatorService.getUsuarioLoginFromCertChain(certChain);
            authenticatorService.signatureAuthentication(usuarioLogin, signature, certChain, true);
            DocumentoBin bin = documentoBinManager.createProcessoDocumentoBin(tituloTermoAdesao, getTermoAdesao());

            List<UsuarioPerfil> perfilAtivoList = usuarioLogin.getUsuarioPerfilAtivoList();
            if (perfilAtivoList != null) {
                UsuarioPerfil perfil = null;
                for (UsuarioPerfil usuarioPerfil : perfilAtivoList) {
                    if ((perfil = usuarioPerfil).getPerfilTemplate().getPapel().getTermoAdesao()) {
                        break;
                    }
                }
                assinaturaDocumentoService.assinarDocumento(bin, perfil, certChain, signature);
                PessoaFisica pessoaFisica = usuarioLogin.getPessoaFisica();
                pessoaFisica.setTermoAdesao(bin);
            }
            documentoBinManager.flush();
            FacesMessages.instance().add(Severity.INFO,
                    Messages.resolveMessage(TERMS_CONDITIONS_SIGN_SUCCESS));
            return "/Painel/list.seam";
        } catch (CertificateExpiredException e) {
            LOG.error(METHOD_ASSINAR_TERMO_ADESAO, e);
            throw new RedirectToLoginApplicationException(Messages.resolveMessage(AuthenticatorService.CERTIFICATE_ERROR_EXPIRED), e);
        } catch (CertificateException e) {
            LOG.error(METHOD_ASSINAR_TERMO_ADESAO, e);
            throw new RedirectToLoginApplicationException(MessageFormat.format(
                            Messages.resolveMessage(AuthenticatorService.CERTIFICATE_ERROR_UNKNOWN),
                            e.getMessage()), e);
        } catch (CertificadoException | LoginException | DAOException e) {
            LOG.error(METHOD_ASSINAR_TERMO_ADESAO, e); 
            throw new RedirectToLoginApplicationException(e.getMessage(), e);
        } catch (AssinaturaException e) {
            LOG.error(METHOD_ASSINAR_TERMO_ADESAO, e);
        }
        return null;
    }

    public String getTermoAdesao() {
        if (termoAdesao == null) {
            Parametro parametro = parametroManager.getParametro(PARAMETRO_TERMO_ADESAO);
            if (parametro != null) {
                tituloTermoAdesao = parametro.getValorVariavel();
                ModeloDocumento modeloDocumento = modeloDocumentoManager.getModeloDocumentoByTitulo(tituloTermoAdesao);
                termoAdesao = modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento);
            }
            if (termoAdesao == null) {
                termoAdesao = "<div><p>TERMO DE ADESÃO</p></div>";
            }
        }
        return this.termoAdesao;
    }

    public String getTermoAdesaoPanelName() {
        return TermoAdesaoAction.PANEL_NAME;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(final String signature) {
        this.signature = signature;
    }

    public String getCertChain() {
        return this.certChain;
    }

    public void setCertChain(final String certChain) {
        this.certChain = certChain;
    }

    public String getMd5Sum() {
        return MD5Encoder.encode(getTermoAdesao());
    }

}
