package br.com.infox.epp.access.crud;

import java.io.Serializable;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.text.MessageFormat;

import javax.security.auth.login.LoginException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.certificado.CertificateSignatures;
import br.com.infox.certificado.bean.CertificateSignatureBundleBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleStatus;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.PapelManager;
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
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.RedirectToLoginApplicationException;
// TODO: Transformar este componente em um manager, despejar atributos persistentes na classe de fronteira responsável pelo login
@Scope(ScopeType.CONVERSATION)
@Name(value = TermoAdesaoAction.NAME)
@Transactional
public class TermoAdesaoAction implements Serializable {
	
    private static final String TERMS_CONDITIONS_SIGN_SUCCESS = "termoAdesao.sign.success";
    private static final String TERMO_ADESAO_CERT_CHAIN_ERROR = "termoAdesao.error.certchain";
    private static final String TERMO_ADESAO_SIGNATURE_ERROR = "termoAdesao.error.signaturechain";
    private static final String METHOD_ASSINAR_TERMO_ADESAO = "termoAdesaoAction.assinarTermoAdesao()";
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
    private Authenticator authenticator;
    @In
    private InfoxMessages infoxMessages;
    @In
    private PapelManager papelManager;

    public String assinarTermoAdesao() {
        try {
        	CertificateSignatureBundleBean bundle = getSignature();
        	if(bundle == null || bundle.getSignatureBeanList() == null || bundle.getSignatureBeanList().get(0) == null){
        		throw new CertificadoException(infoxMessages.get(METHOD_ASSINAR_TERMO_ADESAO));
        	}
        	String certChain = bundle.getSignatureBeanList().get(0).getCertChain();
        	if(certChain == null){
        		throw new CertificadoException(infoxMessages.get(TERMO_ADESAO_CERT_CHAIN_ERROR));
        	}
        	String signature = bundle.getSignatureBeanList().get(0).getSignature();
        	if(signature == null){
        		throw new CertificadoException(infoxMessages.get(TERMO_ADESAO_SIGNATURE_ERROR));
        	}
        	UsuarioLogin usuarioLogin = Authenticator.getUsuarioLogado();
        	if(usuarioLogin == null){
        		usuarioLogin = authenticatorService.getUsuarioLoginFromCertChain(certChain);
        	}
            authenticatorService.signatureAuthentication(usuarioLogin, signature, certChain, true);
            DocumentoBin bin = documentoBinManager.createProcessoDocumentoBin(tituloTermoAdesao, getTermoAdesao());
            
            UsuarioPerfil perfil = papelManager.getPerfilTermoAdesao(usuarioLogin);
            if (perfil != null) {
                assinaturaDocumentoService.assinarDocumento(bin, perfil, certChain, signature);
                PessoaFisica pessoaFisica = usuarioLogin.getPessoaFisica();
                pessoaFisica.setTermoAdesao(bin);
            }
            documentoBinManager.flush();
            FacesMessages.instance().add(Severity.INFO, infoxMessages.get(TERMS_CONDITIONS_SIGN_SUCCESS));
            return authenticator.getCaminhoPainel();
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
    
    public void setTermoAdesao(String termoAdesao) {
		this.termoAdesao = termoAdesao;
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
    	    
    		throw new CertificadoException("termoAdesao.sign.error");
    	}
    	return bundle;
    }

}
