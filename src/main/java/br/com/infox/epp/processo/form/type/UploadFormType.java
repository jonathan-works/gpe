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
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.epp.processo.form.variable.value.UploadValueImpl;
import br.com.infox.epp.processo.form.variable.value.ValueType;
import br.com.infox.ibpm.variable.file.FileVariableHandler;
import br.com.infox.seam.exception.BusinessRollbackException;

public class UploadFormType extends FileFormType {
    
    private static final Logger LOG = Logger.getLogger(UploadFormType.class.getName());
    
    public UploadFormType() {
        super("upload", ValueType.UPLOAD);
    }

    @Override
    public TypedValue convertToFormValue(Object value) {
        if (value == null) {
            return new UploadValueImpl(null);
        }
        if (value instanceof String) {
            value = Integer.valueOf((String) value);
        }
        if (value instanceof Integer) {
            Documento documento = getDocumentoManager().find((Integer) value);
            return new UploadValueImpl(documento);
        }
        return new UploadValueImpl(null);
    }
    
    public void processFileUpload(FileUploadEvent fileUploadEvent) {
        UploadedFile file = fileUploadEvent.getUploadedFile();
        UIComponent uploadFile = fileUploadEvent.getComponent();
        FormField formField = (FormField) uploadFile.getAttributes().get("formField");
        FormData formData = (FormData) uploadFile.getAttributes().get("formData");
        UploadValueImpl typedValue = (UploadValueImpl) formField.getTypedValue();
        try {
            ClassificacaoDocumento classificacao = typedValue.getClassificacaoDocumento();
            getDocumentoUploadService().validaDocumento(file, classificacao, file.getData());
            getFileVariableHandler().gravarDocumento(file, uploadFile.getId(), typedValue, formData.getProcesso());
            formData.setVariable(formField.getId(), typedValue);
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
