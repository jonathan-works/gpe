package br.com.infox.ibpm.variable.file;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.richfaces.model.UploadedFile;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.FileUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.variable.value.UploadValueImpl;
import br.com.infox.epp.processo.home.ProcessoEpaHome;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.exception.BusinessRollbackException;
import br.com.infox.seam.util.ComponentUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class FileVariableHandler {
    
	@Inject
	private DocumentoBinManager documentoBinManager;
	@Inject
	private DocumentoManager documentoManager;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void gravarDocumento(UploadedFile file, String variableFieldName) {
		TaskInstanceHome taskInstanceHome = TaskInstanceHome.instance();
		ProcessoEpaHome processoEpaHome = ComponentUtil.getComponent(ProcessoEpaHome.NAME);
		Integer idDocumentoExistente = (Integer) taskInstanceHome.getValueOfVariableFromTaskInstance(taskInstanceHome.getVariableName(variableFieldName));
        if (idDocumentoExistente != null) {
            try {
                removeDocumento(documentoManager.find(idDocumentoExistente), variableFieldName);
            } catch (DAOException e) {
                throw new BusinessRollbackException(e);
            }
        }
        Documento documento = createDocumento(file, TaskInstanceHome.instance().getVariaveisDocumento().get(variableFieldName).getClassificacaoDocumento());
        try {
            documentoManager.gravarDocumentoNoProcesso(processoEpaHome.getInstance(), documento);
            taskInstanceHome.getInstance().put(variableFieldName, documento.getId());
        } catch (DAOException | BusinessException e) {
            throw new BusinessRollbackException(e);
        }
        taskInstanceHome.update();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removeDocumento(Documento documento, String variableFieldName) throws DAOException {
		DocumentoBinarioManager documentoBinarioManager = ComponentUtil.getComponent(DocumentoBinarioManager.NAME);
		TaskInstanceHome taskInstanceHome = TaskInstanceHome.instance();
		documentoManager.remove(documento);
        documentoBinManager.remove(documento.getDocumentoBin());
        documentoBinarioManager.remove(documento.getDocumentoBin().getId());
        taskInstanceHome.getInstance().put(variableFieldName, null);
        taskInstanceHome.update();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void gravarDocumento(UploadedFile file, String variableFieldName, UploadValueImpl typedValue, Processo processo) {
        if (typedValue.getValue() != null) {
            try {
                removeDocumento(typedValue.getValue());
            } catch (DAOException e) {
                throw new BusinessRollbackException(e);
            }
        }
        Documento documento = createDocumento(file, typedValue.getClassificacaoDocumento());
        try {
            documentoManager.gravarDocumentoNoProcesso(processo, documento);
            typedValue.setValue(documento);
        } catch (DAOException | BusinessException e) {
            throw new BusinessRollbackException(e);
        }
    }
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeDocumento(Documento documento) throws DAOException {
        DocumentoBinarioManager documentoBinarioManager = ComponentUtil.getComponent(DocumentoBinarioManager.NAME);
        documentoManager.remove(documento);
        documentoBinManager.remove(documento.getDocumentoBin());
        documentoBinarioManager.remove(documento.getDocumentoBin().getId());
    }
	
	private Documento createDocumento(UploadedFile file, ClassificacaoDocumento classificacaoDocumento) {
        Documento documento = new Documento();
        documento.setDescricao(file.getName());
        documento.setAnexo(true);
        documento.setDocumentoBin(createDocumentoBin(file));
        documento.setClassificacaoDocumento(classificacaoDocumento);
        documento.setUsuarioInclusao(Authenticator.getUsuarioLogado());
        documento.setLocalizacao(Authenticator.getLocalizacaoAtual());
        return documento;
    }

    private DocumentoBin createDocumentoBin(UploadedFile file) {
        DocumentoBin documentoBin = new DocumentoBin();
        documentoBin.setNomeArquivo(file.getName());
        documentoBin.setExtensao(FileUtil.getFileType(file.getName()));
        documentoBin.setSize(Long.valueOf(file.getSize()).intValue());
        documentoBin.setProcessoDocumento(file.getData());
        documentoBin.setDataInclusao(new Date());
        return documentoBin;
    }
}
