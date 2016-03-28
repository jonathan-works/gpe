package br.com.infox.epp.processo.documento.assinatura;

import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.controller.AbstractController;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Scope(ScopeType.CONVERSATION)
@Name(AssinadorDocumento.NAME)
@ContextDependency
public class AssinadorDocumento extends AbstractController {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "assinadorDocumento";
    private static final LogProvider LOG = Logging.getLogProvider(AssinadorDocumento.class);

    private String certChain;
    private String signature;
    private boolean houveErroAoAssinar = false;

    private Documento documento;
    private DocumentoBin documentoBin;

    @Inject
    private AssinaturaDocumentoService assinaturaDocumentoService;
    @Inject
    private GenericManager genericManager;
    @Inject
    private ActionMessagesService actionMessagesService;

    public String getCertChain() {
        return certChain;
    }

    public void setCertChain(String certChain) {
        this.certChain = certChain;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public boolean isHouveErroAoAssinar() {
        return houveErroAoAssinar;
    }

    public void setHouveErroAoAssinar(boolean houveErroAoAssinar) {
        this.houveErroAoAssinar = houveErroAoAssinar;
    }

    public DocumentoBin getDocumentoBin() {
		return documentoBin;
	}

	public void setDocumentoBin(DocumentoBin documentoBin) {
		this.documentoBin = documentoBin;
	}

	public void assinarDocumento() {
        final FacesMessages messages = FacesMessages.instance();
        try {
            final UsuarioPerfil perfilAtual = Authenticator
                    .getUsuarioPerfilAtual();
            assinaturaDocumentoService.assinarDocumento(documento,
                    perfilAtual, certChain, signature);
            genericManager.update(documento);
            messages.clear();
            messages.add(InfoxMessages.getInstance().get("assinatura.assinadoSucesso"));
        } catch (DAOException e) {
            LOG.error("Não foi possível assinar o documento "
                    + documento, e);
            actionMessagesService.handleDAOException(e);
        } catch (CertificadoException | AssinaturaException e) {
            LOG.error("Não foi possível verificar o certificado do usuário "
                    + Authenticator.getUsuarioLogado(), e);
            messages.clear();
            messages.add(e.getMessage());
            this.setHouveErroAoAssinar(true);
        }

    }

    public boolean isSigned() {
        return assinaturaDocumentoService.isDocumentoAssinado(documento);
    }

    @Override
    public void setId(Object id) {
        super.setId(id);
        documento = genericManager.find(Documento.class, id);
        setDocumentoBin(documento.getDocumentoBin());
    }

}
