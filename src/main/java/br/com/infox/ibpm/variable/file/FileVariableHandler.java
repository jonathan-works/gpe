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
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
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
        Documento documento = createDocumento(file, variableFieldName);
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
