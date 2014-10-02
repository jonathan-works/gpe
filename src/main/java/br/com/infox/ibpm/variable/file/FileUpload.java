package br.com.infox.ibpm.variable.file;

import java.io.IOException;
import java.io.InputStream;

import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;
import org.richfaces.model.UploadedFile;

import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.file.reader.InfoxPdfReader;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.FileUtil;
import br.com.infox.epp.documento.manager.TipoProcessoDocumentoManager;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoBinManager;
import br.com.infox.epp.processo.home.ProcessoEpaHome;
import br.com.infox.ibpm.task.home.TaskInstanceHome;

@Name(FileUpload.NAME)
public class FileUpload implements FileUploadListener {

    public static final String NAME = "fileUpload";
    private static final LogProvider LOG = Logging.getLogProvider(FileUpload.class);
    
    @In
    private DocumentoManager documentoManager;
    @In
    private GenericManager genericManager;
    @In
    private ProcessoDocumentoBinManager processoDocumentoBinManager;
    @In
    private DocumentoBinManager documentoBinManager;
    @In
    private TipoProcessoDocumentoManager tipoProcessoDocumentoManager;
    
    @Override
    public void processFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getUploadedFile();
        UIComponent uploadFile = event.getComponent();
        Integer idDocumentoExistente = (Integer) TaskInstanceHome.instance().getValueOfVariableFromTaskInstance(TaskInstanceHome.instance().getVariableName(uploadFile.getId()));
        if (idDocumentoExistente != null) {
            try {
                Documento doc = documentoManager.find(idDocumentoExistente);
                documentoManager.remove(doc);
                documentoBinManager.remove(idDocumentoExistente);
            } catch (DAOException e) {
                LOG.error("Erro ao remover o documento existente, com id: " + idDocumentoExistente, e);
                throw new AbortProcessingException(e);
            }
        }
        Documento processoDocumento = createDocumento(file, uploadFile.getId());
        try {
            documentoManager.gravarDocumentoNoProcesso(ProcessoEpaHome.instance().getInstance(), processoDocumento);
            documentoBinManager.salvarBinario(processoDocumento.getId(), processoDocumento.getProcessoDocumentoBin().getProcessoDocumento());
            TaskInstanceHome.instance().getInstance().put(uploadFile.getId(), processoDocumento.getId());
        } catch (DAOException e) {
            LOG.error("Não foi possível gravar o documento " + file.getName() + "no processo " + ProcessoEpaHome.instance().getInstance().getIdProcesso(), e);
        }
        TaskInstanceHome.instance().update();
    }
    
    private Documento createDocumento(final UploadedFile file, final String id) {
        Documento pd = new Documento();
        pd.setDescricao(file.getName());
        pd.setAnexo(true);
        pd.setProcessoDocumentoBin(createDocumentoBin(file));
        pd.setTipoProcessoDocumento(TaskInstanceHome.instance().getClassificacoesVariaveisUpload().get(id));
        return pd;
    }

    private ProcessoDocumentoBin createDocumentoBin(final UploadedFile file) {
        ProcessoDocumentoBin pdb = new ProcessoDocumentoBin();
        pdb.setNomeArquivo(file.getName());
        pdb.setExtensao(FileUtil.getFileType(file.getName()));
        pdb.setMd5Documento(MD5Encoder.encode(file.getData()));
        pdb.setSize(Long.valueOf(file.getSize()).intValue());
        pdb.setProcessoDocumento(file.getData());
        pdb.setModeloDocumento(getTextoIndexavel(file));
        return pdb;
    }
    
    private String getTextoIndexavel(final UploadedFile file) {
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
            return InfoxPdfReader.readPdfFromInputStream(inputStream);
        } catch (IOException exception) {
            LOG.error("Não foi possível recuperar o inputStream do arquivo carregado", exception);
        }
        return null;
    }
    
}
