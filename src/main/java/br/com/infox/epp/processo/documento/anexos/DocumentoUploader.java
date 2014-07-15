package br.com.infox.epp.processo.documento.anexos;

import java.io.IOException;
import java.io.InputStream;

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

import com.lowagie.text.pdf.PdfReader;

import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.file.reader.InfoxPdfReader;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.entity.ExtensaoArquivo;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.manager.ExtensaoArquivoManager;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;

@Name(DocumentoUploader.NAME)
@Scope(ScopeType.CONVERSATION)
public class DocumentoUploader extends DocumentoCreator implements FileUploadListener {

    public static final String NAME = "documentoUploader";

    private static final LogProvider LOG = Logging.getLogProvider(DocumentoUploader.class);

    private boolean isValido;

    @In
    private ProcessoDocumentoManager processoDocumentoManager;
    @In
    private DocumentoBinManager documentoBinManager;
    @In
    private ExtensaoArquivoManager extensaoArquivoManager;
    private InputStream inputStream;
    private UploadedFile uploadedFile;
    private TipoProcessoDocumento tipoProcessoDocumento;

    public boolean isValido() {
        return isValido;
    }

    public void setValido(boolean isValido) {
        this.isValido = isValido;
    }

    @Override
    public void newInstance() {
        super.newInstance();
        uploadedFile = null;
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
        bin().setExtensao(getFileType(ui.getName()));
        setValido(isDocumentoBinValido(ui));
        if (isValido()) {
            setUploadedFile(ui);
            bin().setUsuario(Authenticator.getUsuarioLogado());
            bin().setNomeArquivo(ui.getName());
            bin().setMd5Documento(getMD5(ui.getData()));
            bin().setSize(Long.valueOf(ui.getSize()).intValue());
            bin().setProcessoDocumento(ui.getData());
            bin().setModeloDocumento(null);
            FacesMessages.instance().add(Messages.instance().get("processoDocumento.doneLabel"));
        } else {
            newInstance();
            inputStream = null;
            tipoProcessoDocumento = null;
        }
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
        //Removida indexação manual daqui
        newInstance();
        tipoProcessoDocumento = null;
        inputStream = null;
        return pd;
    }

    private boolean isDocumentoBinValido(final UploadedFile file) {
        if (file == null) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Nenhum documento selecionado.");
            return false;
        }
        ExtensaoArquivo extensaoArquivo = extensaoArquivoManager.getTamanhoMaximo(tipoProcessoDocumento, bin().getExtensao());
        if (extensaoArquivo == null) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Extensão de arquivo não permitida.");
            return false;
        }
        if (file.getSize() > extensaoArquivo.getTamanho()) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, "O documento deve ter o tamanho máximo de "
                    + extensaoArquivo.getTamanho() + "bytes!");
            return false;
        }
        if (extensaoArquivo.getPaginavel()) {
            if(validaLimitePorPagina(extensaoArquivo.getTamanhoPorPagina())){
                return true;
            } else {
                FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Não foi possível recuperar as páginas do arquivo.");
                return false;
            }
        }
        return true;
    }
    
    private boolean validaLimitePorPagina(Integer limitePorPagina) {
        PdfReader reader;
        try {
            reader = new PdfReader(inputStream);
            int qtdPaginas = reader.getNumberOfPages();
            for (int i = 1; i <= qtdPaginas; i++) {
                if (reader.getPageContent(i).length > limitePorPagina) {
                    return false;
                }
            }
        } catch (IOException e) {
            LOG.error("Não foi possível recuperar as páginas do arquivo", e);
            return false;
        }
        return true;
    }
    

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public TipoProcessoDocumento getTipoProcessoDocumento() {
        return tipoProcessoDocumento;
    }

    public void setTipoProcessoDocumento(
            TipoProcessoDocumento tipoProcessoDocumento) {
        this.tipoProcessoDocumento = tipoProcessoDocumento;
        getProcessoDocumento().setTipoProcessoDocumento(tipoProcessoDocumento);
    }

}
