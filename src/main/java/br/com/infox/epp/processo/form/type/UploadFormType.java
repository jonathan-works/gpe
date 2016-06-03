package br.com.infox.epp.processo.form.type;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.service.DocumentoUploaderService;
import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.variable.value.FileValue;
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.ibpm.variable.file.FileVariableHandler;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.exception.BusinessRollbackException;
import br.com.infox.seam.path.PathResolver;

public class UploadFormType extends FileFormType {
    
    private static final Logger LOG = Logger.getLogger(UploadFormType.class.getName());
    
    public UploadFormType() {
        super("upload", "/Processo/form/upload.xhtml");
    }

    @Override
    public TypedValue convertToFormValue(Object value) {
        if (value == null) {
            return new FileValue(null);
        }
        if (value instanceof String) {
            value = Integer.valueOf((String) value);
        }
        if (value instanceof Integer) {
            Documento documento = getDocumentoManager().find((Integer) value);
            return new FileValue(documento);
        }
        return new FileValue(null);
    }
    
    public void processFileUpload(FileUploadEvent fileUploadEvent) {
        UploadedFile file = fileUploadEvent.getUploadedFile();
        UIComponent uploadFile = fileUploadEvent.getComponent();
        FormField formField = (FormField) uploadFile.getAttributes().get("formField");
        FormData formData = (FormData) uploadFile.getAttributes().get("formData");
        ClassificacaoDocumento classificacao = formField.getProperty("classificacaoDocumento", ClassificacaoDocumento.class);
        try {
            getDocumentoUploadService().validaDocumento(file, classificacao, file.getData());
            getFileVariableHandler().gravarDocumento(file, uploadFile.getId(), formField, formData.getProcesso());
            formData.setVariable(formField.getId(), formField.getTypedValue());
        } catch (BusinessRollbackException e) {
             LOG.log(Level.SEVERE, "Erro ao remover o documento existente", e);
             if (e.getCause() instanceof DAOException) {
                 getActionMessagesService().handleDAOException((DAOException) e.getCause());
             } else {
                 getActionMessagesService().handleException("Erro ao substituir o documento", e);
             }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            getActionMessagesService().handleGenericException(e, "Registro alterado por outro usuário, tente novamente");
        }
    }
    
    @Override
    public void validate(FormField formField, FormData formData) throws BusinessException {
        FileValue typedValue = (FileValue) formField.getTypedValue(); 
        String required = formField.getProperty("required", String.class);
        if ("true".equals(required) && typedValue.getValue() == null) {
            throw new BusinessException("O arquivo do campo " + formField.getLabel() + " é obrigatório");
        }
        if (typedValue.getValue() != null) {
            super.validate(formField, formData);
        }
    }
    
    public String getUrlDownload(FormField formfield) {
        Documento documento = formfield.getValue(Documento.class);
        PathResolver pathResolver = BeanManager.INSTANCE.getReference(PathResolver.class);
        return String.format("%s/downloadDocumento.seam?id=%d", pathResolver.getContextPath(), documento.getId());
    }
    
    protected DocumentoUploaderService getDocumentoUploadService() {
        return BeanManager.INSTANCE.getReference(DocumentoUploaderService.class);
    }
    
    protected ActionMessagesService getActionMessagesService() {
        return BeanManager.INSTANCE.getReference(ActionMessagesService.class);
    }
    
    protected FileVariableHandler getFileVariableHandler() {
        return BeanManager.INSTANCE.getReference(FileVariableHandler.class);
    }
}
