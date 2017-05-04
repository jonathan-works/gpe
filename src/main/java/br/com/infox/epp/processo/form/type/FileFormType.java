package br.com.infox.epp.processo.form.type;

import java.util.List;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.variable.value.ValueType;
import br.com.infox.ibpm.variable.VariableEditorModeloHandler;
import br.com.infox.seam.exception.BusinessException;

public abstract class FileFormType implements FormType {
    
    protected String name;
    protected String path;
    
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
    	String configuration = (String) formField.getProperties().get("configuration");
    	List<String> codigos = null;
        if (configuration != null && !configuration.isEmpty()) {
            codigos = VariableEditorModeloHandler.fromJson(configuration).getCodigosClassificacaoDocumento(); 
        } 
        List<ClassificacaoDocumento> classificacoes = getClassificacaoDocumentoFacade().getUseableClassificacaoDocumentoVariavel(codigos, false);
        formField.addProperty("classificacoesDocumento", classificacoes);
        if (classificacoes.size() == 1) {
            formField.addProperty("classificacaoDocumento", classificacoes.get(0));
        }
    }
    
    @Override
    public void validate(FormField formField, FormData formData) throws BusinessException {
        String required = formField.getProperty("required", String.class);
        Documento documento = formField.getTypedValue(Documento.class);
        if ("true".equals(required) && (documento == null || documento.getId() == null)) {
            throw new BusinessException("O arquivo do campo " + formField.getLabel() + " é obrigatório");
        }
        if (documento != null && documento.getId() != null) {
            boolean assinaturaVariavelOk = validarAssinaturaDocumento(documento);
            if (!assinaturaVariavelOk) {
                throw new BusinessException(String.format(InfoxMessages.getInstance().get("assinaturaDocumento.faltaAssinatura"), formField.getLabel()));
            }
        }
    }

//    @ExceptionHandled(value = MethodType.UNSPECIFIED)
//    public void assinar() throws DAOException, AssinaturaException {
//        try {
//    		getAssinadorService().assinarToken(tokenToSign, Authenticator.getUsuarioPerfilAtual());
//    		FacesMessages.instance().add(InfoxMessages.getInstance().get("assinatura.assinadoSucesso"));
//        } finally {
//            setTokenToSign(null);
//        }
//    }
    
    protected boolean validarAssinaturaDocumento(Documento documento) {
        Papel papel = Authenticator.getPapelAtual();
        boolean isValid = getAssinaturaDocumentoService().isDocumentoTotalmenteAssinado(documento)
                || !documento.isAssinaturaObrigatoria(papel) || documento.isDocumentoAssinado(papel);
        return isValid;
    }
    
    public boolean podeAssinar(FormField formField) {
        Documento documento = formField.getTypedValue(Documento.class);
        if (documento == null){
            return false;
        }
        Papel papelAtual = Authenticator.getPapelAtual();
        boolean papelPermiteAssinaturaMultipla = documento.papelPermiteAssinaturaMultipla(papelAtual);
        return documento != null && documento.getId() != null 
                && (papelPermiteAssinaturaMultipla && !documento.isDocumentoAssinado(Authenticator.getUsuarioLogado())) ||
                    (!papelPermiteAssinaturaMultipla && documento.isDocumentoAssinavel(papelAtual) && !documento.isDocumentoAssinado(papelAtual)); 
    }
    
    @Override
    public boolean isPersistable() {
        return true;
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
    
}
