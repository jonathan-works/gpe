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
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.FormField;
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
		//ClassificacaoDocumento classificacaoDocumento = TaskInstanceHome.instance().getVariaveisDocumento().get(variableFieldName).getClassificacaoDocumento();
		Documento documentoOriginal = TaskInstanceHome.instance().getVariaveisDocumento().get(variableFieldName);
		Integer idDocumentoExistente = (Integer) taskInstanceHome.getValueOfVariableFromTaskInstance(taskInstanceHome.getVariableName(variableFieldName));
        if (idDocumentoExistente != null) {
            try {
                removeDocumento(documentoManager.find(idDocumentoExistente), variableFieldName);
            } catch (DAOException e) {
                throw new BusinessRollbackException(e);
            }
        }
        Documento documento = createDocumento(file, documentoOriginal);
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
	public void gravarDocumento(UploadedFile file, String variableFieldName, FormField formField, Processo processo) {
	    Documento documento = formField.getTypedValue(Documento.class);
        if (documento != null) {
            try {
                removeDocumento(documento);
            } catch (DAOException e) {
                //throw new BusinessRollbackException(e);
                e.printStackTrace();
            }
        } else {
            documento = new Documento();
        }
        ClassificacaoDocumento classificacaoDocumento = formField.getProperty("classificacaoDocumento", ClassificacaoDocumento.class);
        Pasta pasta = formField.getProperty("pasta", Pasta.class);
        documento.setClassificacaoDocumento(classificacaoDocumento);
        documento = createDocumento(file, documento);
        if ( pasta != null ) {
            documento.setPasta(pasta);
        }
        try {
            documentoManager.gravarDocumentoNoProcesso(processo, documento);
            formField.setValue(documento);
        } catch (DAOException | BusinessException e) {
            e.printStackTrace();
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
	
	private Documento createDocumento(UploadedFile file, Documento documentoOriginal) {
        Documento documento = new Documento();
        documento.setDescricao(file.getName());
        documento.setAnexo(true);
        documento.setDocumentoBin(createDocumentoBin(file));
		if (documentoOriginal.getPasta() != null)
			documento.setPasta(documentoOriginal.getPasta());
        documento.setClassificacaoDocumento(documentoOriginal.getClassificacaoDocumento());
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
