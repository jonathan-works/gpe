package br.com.infox.ibpm.variable.file;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.transaction.SystemException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.transaction.Transaction;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;
import org.richfaces.model.UploadedFile;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.FileUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.home.ProcessoEpaHome;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@Name(FileUpload.NAME)
public class FileUpload implements FileUploadListener {

    public static final String NAME = "fileUpload";
    private static final LogProvider LOG = Logging.getLogProvider(FileUpload.class);
    
    @In
    private DocumentoManager documentoManager;
    @In
    private GenericManager genericManager;
    @In
    private DocumentoBinManager documentoBinManager;
    @In
    private DocumentoBinarioManager documentoBinarioManager;
    @In
    private ClassificacaoDocumentoManager classificacaoDocumentoManager;
    @In
    private ProcessoEpaHome processoEpaHome;
    
    @Override
    @Transactional
    public void processFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getUploadedFile();
        UIComponent uploadFile = event.getComponent();
        Integer idDocumentoExistente = (Integer) TaskInstanceHome.instance().getValueOfVariableFromTaskInstance(TaskInstanceHome.instance().getVariableName(uploadFile.getId()));
        if (idDocumentoExistente != null) {
            try {
                Documento doc = documentoManager.find(idDocumentoExistente);
                documentoManager.remove(doc);
                documentoBinManager.remove(doc.getDocumentoBin());
                documentoBinarioManager.remove(doc.getDocumentoBin().getId());
            } catch (DAOException e) {
            	try {
					Transaction.instance().setRollbackOnly();
				} catch (IllegalStateException | SystemException e1) {
					throw new AbortProcessingException(e1);
				}
                LOG.error("Erro ao remover o documento existente, com id: " + idDocumentoExistente, e);
                String message = e.getDatabaseErrorCode() != null ? e.getLocalizedMessage() : e.getMessage();
                FacesMessages.instance().add("Erro ao substituir o documento: " + message);
                throw new AbortProcessingException(e);
            }
        }
        Documento documento = createDocumento(file, uploadFile.getId());
        try {
            documentoManager.gravarDocumentoNoProcesso(processoEpaHome.getInstance().getProcessoRoot(), documento);
            TaskInstanceHome.instance().getInstance().put(uploadFile.getId(), documento.getId());
        } catch (DAOException e) {
            LOG.error("Não foi possível gravar o documento " + file.getName() + "no processo " + processoEpaHome.getInstance().getIdProcesso(), e);
        } catch (BusinessException e) {
        	FacesMessages.instance().add(e.getMessage());
        }
        TaskInstanceHome.instance().update();
    }
    
    private Documento createDocumento(final UploadedFile file, final String id) {
        Documento pd = new Documento();
        pd.setDescricao(file.getName());
        pd.setAnexo(true);
        pd.setDocumentoBin(createDocumentoBin(file));
        pd.setClassificacaoDocumento(TaskInstanceHome.instance().getVariaveisDocumento().get(id).getClassificacaoDocumento());
        pd.setUsuarioInclusao(Authenticator.getUsuarioLogado());
        pd.setLocalizacao(Authenticator.getLocalizacaoAtual());
        return pd;
    }

    private DocumentoBin createDocumentoBin(final UploadedFile file) {
        DocumentoBin pdb = new DocumentoBin();
        pdb.setNomeArquivo(file.getName());
        pdb.setExtensao(FileUtil.getFileType(file.getName()));
        pdb.setSize(Long.valueOf(file.getSize()).intValue());
        pdb.setProcessoDocumento(file.getData());
        pdb.setDataInclusao(new Date());
        return pdb;
    }
}
