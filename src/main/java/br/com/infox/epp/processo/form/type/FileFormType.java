package br.com.infox.epp.processo.form.type;

import java.util.List;

import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.assinador.AssinadorService;
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
import br.com.infox.epp.processo.form.variable.value.ValueType;
import br.com.infox.seam.exception.BusinessException;

public abstract class FileFormType implements FormType {
    
    protected String name;
    protected String path;
    protected String tokenToSign;
    
    public FileFormType(String name, String path) {
        this.name = name;
        this.path = path;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.FILE;
    }
    
    @Override
    public String getPath() {
        return path;
    }
    
    @Override
    public void performUpdate(FormField formField, FormData formData) {
    }
    
    @Override
    public void performValue(FormField formField, FormData formData) {
        String variableName = formField.getId();
        Integer idFluxo = formData.getProcesso().getNaturezaCategoriaFluxo().getFluxo().getIdFluxo();
        List<ClassificacaoDocumento> classificacoes = getVariavelClassificacaoDocumentoManager().listClassificacoesPublicadasDaVariavel(variableName, idFluxo);
        formField.addProperty("classificacoesDocumento", classificacoes);
        if (classificacoes.size() == 1) {
            formField.addProperty("classificacaoDocumento", classificacoes.get(0));
        }
    }
    
    @Override
    public void validate(FormField formField, FormData formData) throws BusinessException {
        Documento documento = formField.getTypedValue(Documento.class);
        boolean assinaturaVariavelOk = validarAssinaturaDocumento(documento);
        if (!assinaturaVariavelOk) {
            throw new BusinessException(String.format(InfoxMessages.getInstance().get("assinaturaDocumento.faltaAssinatura"), formField.getLabel()));
        }
    }

    @ExceptionHandled(value = MethodType.UNSPECIFIED)
    public void assinar() throws DAOException, AssinaturaException {
        try {
    		getAssinadorService().assinarToken(tokenToSign, Authenticator.getUsuarioPerfilAtual());
    		FacesMessages.instance().add(InfoxMessages.getInstance().get("assinatura.assinadoSucesso"));
        } finally {
            setTokenToSign(null);
        }
    }
    
    protected boolean validarAssinaturaDocumento(Documento documento) {
        Papel papel = Authenticator.getPapelAtual();
        boolean isValid = getAssinaturaDocumentoService().isDocumentoTotalmenteAssinado(documento)
                || !documento.isAssinaturaObrigatoria(papel) || documento.isDocumentoAssinado(papel);
        return isValid;
    }
    
    public boolean podeAssinar(FormField formField) {
        Documento documento = formField.getTypedValue(Documento.class);
        return documento != null && documento.getId() != null 
                && documento.isDocumentoAssinavel(Authenticator.getPapelAtual())
                && !documento.isDocumentoAssinado(Authenticator.getPapelAtual());
    }
    
    public String getTokenToSign() {
        return tokenToSign;
    }

    public void setTokenToSign(String tokenToSign) {
        this.tokenToSign = tokenToSign;
    }

    @Override
    public boolean isPersistable() {
        return true;
    }
    
    private AssinadorService getAssinadorService() {
        return BeanManager.INSTANCE.getReference(AssinadorService.class);
    }

    protected AssinaturaDocumentoService getAssinaturaDocumentoService() {
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
