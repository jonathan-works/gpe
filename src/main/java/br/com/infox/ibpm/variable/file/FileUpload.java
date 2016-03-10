package br.com.infox.ibpm.variable.file;

import javax.faces.component.UIComponent;
import javax.inject.Inject;

import org.jboss.seam.annotations.Name;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;
import org.richfaces.exception.FileUploadException;
import org.richfaces.model.UploadedFile;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.documento.service.DocumentoUploaderService;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessRollbackException;

@Name(FileUpload.NAME)
@ContextDependency
public class FileUpload implements FileUploadListener {

    public static final String NAME = "fileUpload";
    private static final LogProvider LOG = Logging.getLogProvider(FileUpload.class);
    
    @Inject
    private FileVariableHandler fileVariableHandler;
    @Inject
    private ActionMessagesService actionMessagesService;
    @Inject
	private DocumentoUploaderService documentoUploaderService;
    
    @Override
    public void processFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getUploadedFile();
        UIComponent uploadFile = event.getComponent();
        try {
        	ClassificacaoDocumento classificacao = TaskInstanceHome.instance().getVariaveisDocumento().get(uploadFile.getId()).getClassificacaoDocumento();
            documentoUploaderService.validaDocumento(file, classificacao, file.getData());
        	fileVariableHandler.gravarDocumento(file, uploadFile.getId());
        } catch (BusinessRollbackException e) {
        	 LOG.error("Erro ao remover o documento existente", e);
        	 if (e.getCause() instanceof DAOException) {
        		 actionMessagesService.handleDAOException((DAOException) e.getCause());
        	 } else {
        		 actionMessagesService.handleException("Erro ao substituir o documento", e);
        	 }
        } catch (FileUploadException e) {
        	LOG.error("", e);
			actionMessagesService.handleGenericException(e, "Registro alterado por outro usuário, tente novamente");
		} catch (Exception e) {
			LOG.error("", e);
			actionMessagesService.handleGenericException(e, "Registro alterado por outro usuário, tente novamente");
		}
    }
}
