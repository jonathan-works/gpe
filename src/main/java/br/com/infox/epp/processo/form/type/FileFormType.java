package br.com.infox.epp.processo.form.type;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.certificado.CertificateSignatures;
import br.com.infox.certificado.bean.CertificateSignatureBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleBean;
import br.com.infox.certificado.bean.CertificateSignatureBundleStatus;
import br.com.infox.certificado.exception.CertificadoException;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.form.FormData;

public abstract class FileFormType implements FormType {
    
    protected FormData formData; 
    protected Documento documentoToSign;
    protected String tokenToSign;
    
    public FileFormType(FormData formData) {
        this.formData = formData;
    }

    @ExceptionHandled(value = MethodType.UNSPECIFIED)
    public void assinar() throws DAOException, CertificadoException, AssinaturaException {
        CertificateSignatureBundleBean certificateSignatureBundle = getCertificateSignatures().get(tokenToSign);
        if (certificateSignatureBundle.getStatus() != CertificateSignatureBundleStatus.SUCCESS) {
            FacesMessages.instance().add("Erro ao assinar");
        } else {
            CertificateSignatureBean signatureBean = certificateSignatureBundle.getSignatureBeanList().get(0);
            getAssinaturaDocumentoService().assinarDocumento(documentoToSign, Authenticator.getUsuarioPerfilAtual(),
                    signatureBean.getCertChain(), signatureBean.getSignature());
        }
    }
    
    public void setDocumentoToSign(Documento documentoToSign) {
        this.documentoToSign = documentoToSign;
    }

    public void setTokenToSign(String tokenToSign) {
        this.tokenToSign = tokenToSign;
    }

    private CertificateSignatures getCertificateSignatures() {
        return BeanManager.INSTANCE.getReference(CertificateSignatures.class);
    }
    
    private AssinaturaDocumentoService getAssinaturaDocumentoService() {
        return BeanManager.INSTANCE.getReference(AssinaturaDocumentoService.class);
    }
    
    protected DocumentoManager getDocumentoManager() {
        return BeanManager.INSTANCE.getReference(DocumentoManager.class);
    }
    
}
