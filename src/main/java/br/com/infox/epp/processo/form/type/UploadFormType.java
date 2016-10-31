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
import br.com.infox.ibpm.variable.file.FileVariableHandler;
import br.com.infox.seam.exception.BusinessRollbackException;
import br.com.infox.seam.path.PathResolver;

public class UploadFormType extends FileFormType {
    
    private static final Logger LOG = Logger.getLogger(UploadFormType.class.getName());
    
    public UploadFormType() {
        super("upload", "/Processo/form/upload.xhtml");
    }

    @Override
    public Object convertToFormValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            value = Integer.valueOf((String) value);
        }
        if (value instanceof Integer) {
            Documento documento = getDocumentoManager().find((Integer) value);
            return documento;
        }
        return null;
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
            formData.setVariable(formField.getId(), new TypedValue(formField.getValue(), formField.getType().getValueType()));
        } catch (BusinessRollbackException e) {
             LOG.log(Level.SEVERE, "Erro ao remover o documento existente", e);
             if (e.getCause() instanceof DAOException) {
                 getActionMessagesService().handleDAOException((DAOException) e.getCause());
             } else {
                 getActionMessagesService().handleException("Erro ao substituir o documento." + e.getMessage(), e);
             }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
            getActionMessagesService().handleGenericException(e, "Registro alterado por outro usuário, tente novamente");
        }
    }
    
    public String getUrlDownload(FormField formfield) {
        Documento documento = formfield.getTypedValue(Documento.class);
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
