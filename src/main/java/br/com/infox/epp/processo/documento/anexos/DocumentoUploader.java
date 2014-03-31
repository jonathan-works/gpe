package br.com.infox.epp.processo.documento.anexos;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.Messages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;
import org.richfaces.model.UploadedFile;

import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.file.reader.InfoxPdfReader;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.index.InfoxDocumentIndexer;

@Name(DocumentoUploader.NAME)
@Scope(ScopeType.CONVERSATION)
public class DocumentoUploader extends DocumentoCreator implements FileUploadListener {

    public static final String NAME = "documentoUploader";
    private static final int TAMANHO_MAXIMO_ARQUIVO = 2097152;

    private static final LogProvider LOG = Logging.getLogProvider(DocumentoUploader.class);

    private boolean isValido;

    @In
    private ProcessoDocumentoManager processoDocumentoManager;
    @In
    private DocumentoBinManager documentoBinManager;
    private InputStream inputStream;

    public boolean isValido() {
        return isValido;
    }

    public void setValido(boolean isValido) {
        this.isValido = isValido;
    }

    @Override
    protected void newInstance() {
        super.newInstance();
        isValido = false;
    }

    @Override
    public void processFileUpload(FileUploadEvent fileUploadEvent) {
        newInstance();
        final UploadedFile ui = fileUploadEvent.getUploadedFile();
        try {
            inputStream = ui.getInputStream();
        } catch (IOException e) {
            LOG.error("Não foi possível recuperar o inputStream do arquivo carregado", e);
        }
        setValido(isDocumentoBinValido(ui));
        bin().setUsuario(Authenticator.getUsuarioLogado());
        bin().setNomeArquivo(ui.getName());
        bin().setExtensao(getFileType(ui.getName()));
        bin().setMd5Documento(getMD5(ui.getData()));
        bin().setSize(Long.valueOf(ui.getSize()).intValue());
        bin().setProcessoDocumento(ui.getData());
        bin().setModeloDocumento(null);
        FacesMessages.instance().add(Messages.instance().get("processoDocumento.doneLabel"));
    }

    private ProcessoDocumentoBin bin() {
        if (getProcessoDocumento().getProcessoDocumentoBin() == null) {
            getProcessoDocumento().setProcessoDocumentoBin(new ProcessoDocumentoBin());
        }
        return getProcessoDocumento().getProcessoDocumentoBin();
    }

    private String getFileType(String nomeArquivo) {
        String ret = "";
        if (nomeArquivo != null) {
            ret = nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1);
        }
        return ret;
    }

    private String getMD5(byte[] data) {
        return MD5Encoder.encode(data);
    }

    @Override
    protected LogProvider getLogger() {
        return LOG;
    }

    @Override
    protected ProcessoDocumento gravarDocumento() throws DAOException {
        String texto = InfoxPdfReader.readPdfFromInputStream(inputStream);
        ProcessoDocumento pd = processoDocumentoManager.gravarDocumentoNoProcesso(getProcesso(), getProcessoDocumento());
        bin().setModeloDocumento(texto);
        documentoBinManager.salvarBinario(getProcessoDocumento().getIdProcessoDocumento(), bin().getProcessoDocumento());
            try {
                InfoxDocumentIndexer indexer = new InfoxDocumentIndexer();
                Map<String, String> fields = new HashMap<String, String>();
                Map<String, String> storedfields = new HashMap<String, String>();
                fields.put("conteudo", texto);
                storedfields.put("nomeArquivo", pd.getProcessoDocumento());
                storedfields.put("idProcesso", pd.getProcesso().getIdProcesso() + "");
                if (TaskInstanceHome.instance().getTaskId() != null) {
                    storedfields.put("taskId", TaskInstanceHome.instance().getTaskId() + "");
                }
                indexer.index(pd.getIdProcessoDocumento() + "", storedfields, fields);
            } catch (IOException e) {
                LOG.error("Não foi possível indexar o documento "
                        + pd.getProcessoDocumento(), e);
            }
        return pd;
    }

    private boolean isDocumentoBinValido(final UploadedFile file) {
        if (file == null) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Nenhum documento selecionado.");
            return false;
        }
        if (file.getSize() > TAMANHO_MAXIMO_ARQUIVO) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "O documento deve ter o tamanho máximo de 1.5MB!");
            return false;
        }
        return true;
    }

}
