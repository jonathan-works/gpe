package br.com.infox.epp.access.crud;

import java.io.Serializable;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.text.MessageFormat;
import java.util.List;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.access.service.AuthenticatorService;
import br.com.infox.epp.assinador.AssinadorService;
import br.com.infox.epp.assinador.DadosAssinatura;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.certificado.entity.TipoAssinatura;
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
@ContextDependency
public class TermoAdesaoAction implements Serializable {
	
    private static final String TERMS_CONDITIONS_SIGN_SUCCESS = "termoAdesao.sign.success";
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
    @Inject
    private AssinadorService assinadorService;
    @In
    private Authenticator authenticator;
    @In
    private InfoxMessages infoxMessages;
    @In
    private PapelManager papelManager;

    public String assinarTermoAdesao() {
        try {
        	List<DadosAssinatura> dadosAssinaturaList = assinadorService.getDadosAssinatura(token);
        	
        	DadosAssinatura dadosAssinatura = dadosAssinaturaList.get(0);
        	String certChain = dadosAssinatura.getCertChainBase64();
        	String signature = dadosAssinatura.getAssinaturaBase64();
        	UsuarioLogin usuarioLogin = Authenticator.getUsuarioLogado();
        	if(usuarioLogin == null){
        		usuarioLogin = authenticatorService.getUsuarioLoginFromCertChain(certChain);
        	}
            authenticatorService.signatureAuthentication(usuarioLogin, signature, certChain, true);
            DocumentoBin bin = documentoBinManager.createProcessoDocumentoBin(tituloTermoAdesao, getTermoAdesao());
            
            UsuarioPerfil perfil = papelManager.getPerfilTermoAdesao(usuarioLogin);
            if (perfil != null) {
                assinaturaDocumentoService.assinarDocumento(bin, perfil, certChain, signature, TipoAssinatura.PKCS7, dadosAssinatura.getSignedData(), dadosAssinatura.getTipoSignedData());
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
            throw new RedirectToLoginApplicationException(e.getMessage(), e);
        }
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
}
