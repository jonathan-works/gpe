package br.com.infox.epp.access.crud;

import java.io.Serializable;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.Identity;

import br.com.infox.certificado.CertificateSignatures;
import br.com.infox.certificado.bean.CertificateSignatureBundleBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleStatus;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.messages.InfoxMessages;
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
import br.com.infox.epp.system.EppMessagesContextLoader;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.manager.ParametroManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.RedirectToLoginApplicationException;
import br.com.infox.seam.util.ComponentUtil;
// TODO: Transformar este componente em um manager, despejar atributos persistentes na classe de fronteira responsável pelo login
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

    private String token;
    private String termoAdesao;
    private String tituloTermoAdesao;

    @In
    private ParametroManager parametroManager;
    @In
    private ModeloDocumentoManager modeloDocumentoManager;
    @In
    private AuthenticatorService authenticatorService;
    @In
    private DocumentoBinManager documentoBinManager;
    @In
    private AssinaturaDocumentoService assinaturaDocumentoService;
    @In
    private PessoaFisicaManager pessoaFisicaManager;
    @In
    private CertificateSignatures certificateSignatures;
    @In
    private InfoxMessages infoxMessages;

    public String assinarTermoAdesao() {
        try {
        	CertificateSignatureBundleBean bundle = getSignature();
        	String certChain = bundle.getSignatureBeanList().get(0).getCertChain();
        	String signature = bundle.getSignatureBeanList().get(0).getSignature();
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
                    infoxMessages.get(TERMS_CONDITIONS_SIGN_SUCCESS));
            if (Identity.instance().hasRole("usuarioExterno")) {
                return "/PainelExterno/list.seam";
            } else {
                return "/Painel/list.seam";
            }
        } catch (CertificateExpiredException e) {
            LOG.error(METHOD_ASSINAR_TERMO_ADESAO, e);
            throw new RedirectToLoginApplicationException(infoxMessages.get(AuthenticatorService.CERTIFICATE_ERROR_EXPIRED), e);
        } catch (CertificateException e) {
            LOG.error(METHOD_ASSINAR_TERMO_ADESAO, e);
            throw new RedirectToLoginApplicationException(MessageFormat.format(
                            infoxMessages.get(AuthenticatorService.CERTIFICATE_ERROR_UNKNOWN),
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

    public String getToken() {
		return token;
	}
    
    public void setToken(String token) {
		this.token = token;
	}
    
    public String getMd5Sum() {
        return MD5Encoder.encode(getTermoAdesao());
    }
    
    private CertificateSignatureBundleBean getSignature() throws CertificadoException {
    	CertificateSignatureBundleBean bundle = certificateSignatures.get(token);
    	if (bundle == null || bundle.getStatus() != CertificateSignatureBundleStatus.SUCCESS) {
    		Map<String, String> eppmessages = ComponentUtil.getComponent(EppMessagesContextLoader.EPP_MESSAGES);
    		throw new CertificadoException(eppmessages.get("termoAdesao.sign.error"));
    	}
    	return bundle;
    }

}
