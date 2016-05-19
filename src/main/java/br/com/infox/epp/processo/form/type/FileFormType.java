package br.com.infox.epp.processo.form.type;

import java.util.List;

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
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.fluxo.manager.VariavelClassificacaoDocumentoManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.variable.value.FileTypedValue;
import br.com.infox.epp.processo.form.variable.value.ValueType;

public abstract class FileFormType implements FormType {
    
    protected String name;
    protected ValueType valueType;
    protected Documento documentoToSign;
    protected String tokenToSign;
    
    public FileFormType(String name, ValueType valueType) {
        this.name = name;
        this.valueType = valueType;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public ValueType getValueType() {
        return valueType;
    }
    
    @Override
    public void performUpdate(FormField formField, FormData formData) {
        
    }
    
    @Override
    public void performValue(FormField formField, FormData formData) {
        FileTypedValue typedValue = (FileTypedValue) formField.getTypedValue();
        String variableName = formField.getId();
        Integer idFluxo = formData.getProcesso().getNaturezaCategoriaFluxo().getFluxo().getIdFluxo();
        List<ClassificacaoDocumento> classificacoes = getVariavelClassificacaoDocumentoManager().listClassificacoesPublicadasDaVariavel(variableName, idFluxo);
        typedValue.setClassificacoesDocumento(classificacoes);
        if (classificacoes.size() == 1) {
            typedValue.setClassificacaoDocumento(classificacoes.get(0));
        }
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
    
    protected DocumentoBinManager getDocumentoBinManager() {
        return BeanManager.INSTANCE.getReference(DocumentoBinManager.class);
    }
    
    protected ClassificacaoDocumentoFacade getClassificacaoDocumentoFacade() {
        return BeanManager.INSTANCE.getReference(ClassificacaoDocumentoFacade.class);
    }
    
    protected VariavelClassificacaoDocumentoManager getVariavelClassificacaoDocumentoManager() {
        return BeanManager.INSTANCE.getReference(VariavelClassificacaoDocumentoManager.class);
    }
    
}
