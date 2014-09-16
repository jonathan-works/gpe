package br.com.infox.epp.processo.documento.anexos;

import static java.text.MessageFormat.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

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
import br.com.infox.epp.documento.entity.ExtensaoArquivo;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.manager.ExtensaoArquivoManager;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;

import com.lowagie.text.pdf.PdfReader;

@Name(DocumentoUploader.NAME)
@Scope(ScopeType.CONVERSATION)
public class DocumentoUploader extends DocumentoCreator implements FileUploadListener {

    public static final String NAME = "documentoUploader";
    private static final String NOME_DOCUMENTO_DECORATION = "inputProcessoDocumentoPdfDecoration";
    private static final String NOME_DOCUMENTO = "inputProcessoDocumentoPdf";
    private static final String CLASSIFICACAO_DOCUMENTO_DECORATION = "tipoProcessoDocumentoPdfDecoration";
    private static final String CLASSIFICACAO_DOCUMENTO = "tipoProcessoDocumentoPdfDecoration:tipoProcessoDocumentoPdf";
    private static final String FILE_UPLOAD = "tipoDocumentoDivPdf";
    

    private static final LogProvider LOG = Logging.getLogProvider(DocumentoUploader.class);

    private boolean isValido;

    @In
    private ProcessoDocumentoManager processoDocumentoManager;
    @In
    private DocumentoBinManager documentoBinManager;
    @In
    private ExtensaoArquivoManager extensaoArquivoManager;
    
    private UploadedFile uploadedFile;
    private TipoProcessoDocumento tipoProcessoDocumento;
    private byte[] pdf;

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
        pdf = null;
    }

    @Override
    public void processFileUpload(FileUploadEvent fileUploadEvent) {
        newInstance();
        final UploadedFile ui = fileUploadEvent.getUploadedFile();
        try {
            InputStream inputStream = ui.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            pdf = bos.toByteArray();
        } catch (IOException e) {
            LOG.error("Não foi possível recuperar o inputStream do arquivo carregado", e);
            FacesMessages.instance().add("Erro no upload do arquivo, tente novamente.");
            return;
        }
        bin().setExtensao(getFileType(ui.getName()));
        setValido(isDocumentoBinValido(ui));
        if (isValido()) {
            setUploadedFile(ui);
            bin().setNomeArquivo(ui.getName());
            bin().setMd5Documento(getMD5(ui.getData()));
            bin().setSize(Long.valueOf(ui.getSize()).intValue());
            bin().setProcessoDocumento(ui.getData());
            bin().setModeloDocumento(null);
            FacesMessages.instance().add(Messages.instance().get("processoDocumento.doneLabel"));
        } else {
            newInstance();
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
        String texto = InfoxPdfReader.readPdfFromByteArray(pdf);
        ProcessoDocumento pd = processoDocumentoManager.gravarDocumentoNoProcesso(getProcesso(), getProcessoDocumento());
        bin().setModeloDocumento(texto);
        documentoBinManager.salvarBinario(bin().getIdProcessoDocumentoBin(), bin().getProcessoDocumento());
        //Removida indexação manual daqui
        newInstance();
        tipoProcessoDocumento = null;
        setValido(false);
        return pd;
    }

    private boolean isDocumentoBinValido(final UploadedFile file) {
        if (file == null) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, Messages.instance().get("documentoUploader.error.noFile"));
            return false;
        }
        ExtensaoArquivo extensaoArquivo = extensaoArquivoManager.getTamanhoMaximo(tipoProcessoDocumento, bin().getExtensao());
        if (extensaoArquivo == null) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, Messages.instance().get("documentoUploader.error.invalidExtension"));
            return false;
        }
        if ((file.getSize() / 1024F) > extensaoArquivo.getTamanho()) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, format(Messages.instance().get("documentoUploader.error.invalidFileSize"), extensaoArquivo.getTamanho()));
            return false;
        }
        if (extensaoArquivo.getPaginavel()) {
            if(validaLimitePorPagina(extensaoArquivo.getTamanhoPorPagina())){
                return true;
            } else {
                FacesMessages.instance().add(StatusMessage.Severity.ERROR, Messages.instance().get("documentoUploader.error.notPaginable"));
                return false;
            }
        }
        return true;
    }
    
    private boolean validaLimitePorPagina(Integer limitePorPagina) {
        PdfReader reader;
        try {
            reader = new PdfReader(pdf);
            int qtdPaginas = reader.getNumberOfPages();
            for (int i = 1; i <= qtdPaginas; i++) {
                if ((reader.getPageContent(i).length / 1024F) > limitePorPagina) {
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
    
    @Override
    public void clear() {
        super.clear();
        this.tipoProcessoDocumento = null;
    }
    
    public void podeRenderizar(AjaxBehaviorEvent ajaxBehaviorEvent) {
        UIInput input = (UIInput) ajaxBehaviorEvent.getComponent();
        UIInput input2;
        UIComponent form = input.getParent();
        while (!(form instanceof UIForm)) {
            form = form.getParent();
        }
        if (input.getClientId().endsWith(NOME_DOCUMENTO)) {
            
            input2 = (UIInput) form.findComponent(CLASSIFICACAO_DOCUMENTO_DECORATION).findComponent(CLASSIFICACAO_DOCUMENTO);
        } else {
            input2 = (UIInput) form.findComponent(NOME_DOCUMENTO_DECORATION).findComponent(NOME_DOCUMENTO);
        }
        if (input.getValue() != null && input2.getValue() != null) {
            Collection<String> ids = FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds();
            ids.add(input.getClientId());
            ids.add(input2.getClientId());
            ids.add(form.findComponent(FILE_UPLOAD).getClientId());
        }
    }

}
