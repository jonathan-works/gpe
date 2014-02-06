package br.com.infox.epp.processo.documento.assinatura;

import java.util.Date;

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
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;

@Name(AssinadorDocumento.NAME)
@Scope(ScopeType.CONVERSATION)
public class AssinadorDocumento extends AbstractController {
    
    private static final long serialVersionUID = 1L;
    public static final String NAME = "assinadorDocumento";
    private static final LogProvider LOG = Logging.getLogProvider(AssinadorDocumento.class);

    
    private String certChain;
    private String signature;
    private boolean houveErroAoAssinar = false;
    
    private ProcessoDocumento processoDocumento;
    private ProcessoDocumentoBin processoDocumentoBin;
    
    @In AssinaturaDocumentoService assinaturaDocumentoService;
    @In GenericManager genericManager;
    
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

    public void setProcessoDocumentoBin(ProcessoDocumentoBin processoDocumentoBin) {
        this.processoDocumentoBin = processoDocumentoBin;
    }

    public void assinarDocumento() {
        FacesMessages.instance().clear();
        try {
            assinaturaDocumentoService.verificaCertificadoUsuarioLogado(getCertChain(), Authenticator.getUsuarioLogado());
        } catch (CertificadoException | AssinaturaException e) {
            LOG.error("Não foi possível verificar o certificado do usuário "
                    + Authenticator.getUsuarioLogado(), e);
            FacesMessages.instance().clear();
            FacesMessages.instance().add(e.getMessage());
            this.setHouveErroAoAssinar(true);
            return;
        }
//        setId(processoDocumento.getProcessoDocumentoBin().getIdProcessoDocumentoBin());
        processoDocumento.setLocalizacao(Authenticator.getLocalizacaoAtual());
        processoDocumento.setPapel(Authenticator.getPapelAtual());
        getProcessoDocumentoBin().setUsuarioUltimoAssinar(Authenticator.getUsuarioLogado().getNomeUsuario());
        getProcessoDocumentoBin().setSignature(getSignature());
        getProcessoDocumentoBin().setCertChain(getCertChain());
        getProcessoDocumentoBin().setDataInclusao(new Date());
        processoDocumento.setProcessoDocumentoBin(getProcessoDocumentoBin());
        try {
            genericManager.update(processoDocumento);
        } catch (DAOException e) {
            LOG.error("Não foi possível assinar o documento " + processoDocumento, e);
        }
        FacesMessages.instance().add(Messages.instance().get("assinatura.assinadoSucesso"));
    }
    
    @Override
    public void setId(Object id) {
        super.setId(id);
        processoDocumento = genericManager.find(ProcessoDocumento.class, id);
        setProcessoDocumentoBin(processoDocumento.getProcessoDocumentoBin());
    }

}
