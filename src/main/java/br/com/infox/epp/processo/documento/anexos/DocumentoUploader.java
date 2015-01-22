package br.com.infox.epp.processo.documento.anexos;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.commons.io.IOUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;
import org.richfaces.model.UploadedFile;

import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.file.reader.InfoxPdfReader;
import br.com.infox.core.messages.Messages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ExtensaoArquivo;
import br.com.infox.epp.documento.manager.ExtensaoArquivoManager;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;

import com.lowagie.text.pdf.PdfReader;

@Scope(ScopeType.CONVERSATION)
@Name(DocumentoUploader.NAME)
@AutoCreate
public class DocumentoUploader extends DocumentoCreator implements FileUploadListener, Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "documentoUploader";
    private static final String NOME_DOCUMENTO_DECORATION = "inputProcessoDocumentoPdfDecoration";
    private static final String NOME_DOCUMENTO = "inputProcessoDocumentoPdf";
    private static final String CLASSIFICACAO_DOCUMENTO_DECORATION = "tipoProcessoDocumentoPdfDecoration";
    private static final String CLASSIFICACAO_DOCUMENTO = "tipoProcessoDocumentoPdfDecoration:tipoProcessoDocumentoPdf";
    private static final String FILE_UPLOAD = "tipoDocumentoDivPdf";
    
    private static final LogProvider LOG = Logging.getLogProvider(DocumentoUploader.class);

    private boolean isValido;

    @In
    private DocumentoManager documentoManager;
    @In
    private DocumentoBinarioManager documentoBinarioManager;
    @In
    private ExtensaoArquivoManager extensaoArquivoManager;
    
    private UploadedFile uploadedFile;
    private ClassificacaoDocumento classificacaoDocumento;
    private byte[] pdf;
    
    public void onChangeClassificacaoDocumento(AjaxBehaviorEvent ajaxBehaviorEvent){
    	clearUploadFile();
        podeRenderizar(ajaxBehaviorEvent);
    }
    
    public void clearUploadFile(){
    	getDocumento().setDocumentoBin(new DocumentoBin());
    	setValido(false);
    	setUploadedFile(null);
    	pdf = null;
    }

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
        final UploadedFile ui = fileUploadEvent.getUploadedFile();
        try {
            pdf = IOUtils.toByteArray(ui.getInputStream());
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
            FacesMessages.instance().add(Messages.resolveMessage("processoDocumento.doneLabel"));
        } else {
            newInstance();
        }
    }

    private DocumentoBin bin() {
        if (getDocumento().getDocumentoBin() == null) {
            getDocumento().setDocumentoBin(new DocumentoBin());
        }
        return getDocumento().getDocumentoBin();
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
    protected Documento gravarDocumento() throws DAOException {
        String texto = InfoxPdfReader.readPdfFromByteArray(pdf);
        Documento pd = documentoManager.gravarDocumentoNoProcesso(getProcesso(), getDocumento());
        bin().setModeloDocumento(texto);
        documentoBinarioManager.salvarBinario(bin().getId(), bin().getProcessoDocumento());
        //Removida indexação manual daqui
        newInstance();
        classificacaoDocumento = null;
        setValido(false);
        return pd;
    }

    private boolean isDocumentoBinValido(final UploadedFile file) {
        if (file == null) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, Messages.resolveMessage("documentoUploader.error.noFile"));
            return false;
        }
        ExtensaoArquivo extensaoArquivo = extensaoArquivoManager.getTamanhoMaximo(classificacaoDocumento, bin().getExtensao());
        if (extensaoArquivo == null) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, Messages.resolveMessage("documentoUploader.error.invalidExtension"));
            return false;
        }
        if ((file.getSize() / 1024F) > extensaoArquivo.getTamanho()) {
            FacesMessages.instance().add(StatusMessage.Severity.ERROR, format(Messages.resolveMessage("documentoUploader.error.invalidFileSize"), extensaoArquivo.getTamanho()));
            return false;
        }
        if (extensaoArquivo.getPaginavel()) {
            if(validaLimitePorPagina(extensaoArquivo.getTamanhoPorPagina())){
                return true;
            } else {
                FacesMessages.instance().add(StatusMessage.Severity.ERROR, Messages.resolveMessage("documentoUploader.error.notPaginable"));
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

    public ClassificacaoDocumento getClassificacaoDocumento() {
        return classificacaoDocumento;
    }

    public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
        this.classificacaoDocumento = classificacaoDocumento;
        getDocumento().setClassificacaoDocumento(classificacaoDocumento);
    }
    
    @Override
    public void clear() {
        super.clear();
        this.classificacaoDocumento = null;
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
