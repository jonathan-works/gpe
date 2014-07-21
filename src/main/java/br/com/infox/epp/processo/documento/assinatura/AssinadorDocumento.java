package br.com.infox.epp.processo.documento.assinatura;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.Messages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.controller.AbstractController;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;

@Name(AssinadorDocumento.NAME)
@Scope(ScopeType.CONVERSATION)
public class AssinadorDocumento extends AbstractController {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "assinadorDocumento";
    private static final LogProvider LOG = Logging
            .getLogProvider(AssinadorDocumento.class);

    private String certChain;
    private String signature;
    private boolean houveErroAoAssinar = false;

    private ProcessoDocumento processoDocumento;
    private ProcessoDocumentoBin processoDocumentoBin;

    @In
    AssinaturaDocumentoService assinaturaDocumentoService;
    @In
    GenericManager genericManager;

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

    public ProcessoDocumentoBin getProcessoDocumentoBin() {
        return processoDocumentoBin;
    }

    public void setProcessoDocumentoBin(
            ProcessoDocumentoBin processoDocumentoBin) {
        this.processoDocumentoBin = processoDocumentoBin;
    }

    public void assinarDocumento() {
        final FacesMessages messages = FacesMessages.instance();
        try {
            final UsuarioPerfil perfilAtual = Authenticator
                    .getUsuarioPerfilAtual();
            assinaturaDocumentoService.assinarDocumento(processoDocumento,
                    perfilAtual, certChain, signature);
            genericManager.update(processoDocumento);
            messages.clear();
            messages.add(Messages.instance().get("assinatura.assinadoSucesso"));
        } catch (DAOException e) {
            LOG.error("Não foi possível assinar o documento "
                    + processoDocumento, e);
        } catch (CertificadoException | AssinaturaException e) {
            LOG.error("Não foi possível verificar o certificado do usuário "
                    + Authenticator.getUsuarioLogado(), e);
            messages.clear();
            messages.add(e.getMessage());
            this.setHouveErroAoAssinar(true);
        }

    }

    public boolean isSigned() {
        return assinaturaDocumentoService
                .isDocumentoAssinado(processoDocumento);
    }

    @Override
    public void setId(Object id) {
        super.setId(id);
        processoDocumento = genericManager.find(ProcessoDocumento.class, id);
        setProcessoDocumentoBin(processoDocumento.getProcessoDocumentoBin());
    }

}
