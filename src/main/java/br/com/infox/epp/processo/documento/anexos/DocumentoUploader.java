package br.com.infox.epp.processo.documento.anexos;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;
import org.richfaces.model.UploadedFile;

import com.lowagie.text.pdf.PdfReader;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ExtensaoArquivo;
import br.com.infox.epp.documento.manager.ExtensaoArquivoManager;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@Named
@ViewScoped
public class DocumentoUploader extends DocumentoCreator implements FileUploadListener, Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "documentoUploader";
	private static final String NOME_DOCUMENTO_DECORATION = "inputProcessoDocumentoPdfDecoration";
	private static final String NOME_DOCUMENTO = "inputProcessoDocumentoPdf";
	private static final String CLASSIFICACAO_DOCUMENTO_DECORATION = "tipoProcessoDocumentoPdfDecoration";
	private static final String CLASSIFICACAO_DOCUMENTO = "tipoProcessoDocumentoPdfDecoration:tipoProcessoDocumentoPdf";
	private static final String FILE_UPLOAD = "tipoDocumentoDivPdf";
	private static final LogProvider LOG = Logging.getLogProvider(DocumentoUploader.class);

	@Inject
	private InfoxMessages infoxMessages;
	@Inject
	private DocumentoManager documentoManager;
	@Inject
	private ExtensaoArquivoManager extensaoArquivoManager;

	private ClassificacaoDocumento classificacaoDocumento;
	private boolean isValido;

	private List<Documento> documentos = new ArrayList<>();

	public void onChangeClassificacaoDocumento(AjaxBehaviorEvent ajaxBehaviorEvent) {
		clearUploadFile();
		podeRenderizar(ajaxBehaviorEvent);
	}

	public void clearUploadFile() {
		getDocumento().setDocumentoBin(new DocumentoBin());
		documentos.clear();
		setValido(false);
	}

	public boolean isValido() {
		return isValido;
	}

	public void setValido(boolean isValido) {
		this.isValido = isValido;
	}

	private void newDocumento() {
		setDocumento(new Documento());
		getDocumento().setAnexo(true);
		getDocumento().setDocumentoBin(new DocumentoBin());
		getDocumento().setClassificacaoDocumento(classificacaoDocumento);
	}

	public void newInstance(boolean limparDocumentos) {
		super.newInstance();
		newDocumento();
		isValido = false;
		setPasta(null);
		if (limparDocumentos) {
			documentos.clear();
		}
	}

	@Override
	public void newInstance() {
		newInstance(true);
	}

	public void processFileUpload(FileUploadEvent fileUploadEvent) {
		final UploadedFile ui = fileUploadEvent.getUploadedFile();
		byte[] pdf = null;
		try {
			pdf = IOUtils.toByteArray(ui.getInputStream());
		} catch (IOException e) {
			LOG.error("Não foi possível recuperar o inputStream do arquivo carregado", e);
			FacesMessages.instance().add("Erro no upload do arquivo, tente novamente.");
			return;
		}
		if (documentos.size() > 0) {
			newDocumento();
		}
		bin().setExtensao(getFileType(ui.getName()));
		setValido(isDocumentoBinValido(ui, pdf));
		if (isValido()) {
			if (getDocumento().getDescricao() == null) {
				getDocumento().setDescricao(ui.getName());
			}
			bin().setNomeArquivo(ui.getName());
			bin().setSize(Long.valueOf(ui.getSize()).intValue());
			bin().setProcessoDocumento(ui.getData());
			FacesMessages.instance().add(infoxMessages.get("processoDocumento.uploadCompleted"));
			documentos.add(getDocumento());
		} else {
			newInstance(false);
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

	@Override
	protected LogProvider getLogger() {
		return LOG;
	}

	private Documento gravarDocumento(Documento documento) throws DAOException {
		return documentoManager.gravarDocumentoNoProcesso(getProcesso(), documento, getPasta());
	}

	@Override
	protected Documento gravarDocumento() throws DAOException {
		Documento pd = gravarDocumento(getDocumento());
		// Removida indexação manual daqui
		newInstance();
		setClassificacaoDocumento(null);
		setValido(false);
		return pd;
	}

	@Override
	public void persist() {
		try {
			for (Documento documento : documentos) {
				documento = gravarDocumento(documento);
				getDocumentosDaSessao().add(documento);
			}
		} catch (DAOException | BusinessException e) {
			getLogger().error("Não foi possível gravar o documento " + getDocumento() + " no processo " + getProcesso(),
					e);
		}
		newInstance();
	}

	private boolean isDocumentoBinValido(final UploadedFile file, byte[] dadosArquivo) {
		if (file == null) {
			FacesMessages.instance().add(StatusMessage.Severity.ERROR,
					infoxMessages.get("documentoUploader.error.noFile"));
			return false;
		}
		ExtensaoArquivo extensaoArquivo = extensaoArquivoManager.getTamanhoMaximo(classificacaoDocumento,
				bin().getExtensao());
		if (extensaoArquivo == null) {
			FacesMessages.instance().add(StatusMessage.Severity.ERROR,
					infoxMessages.get("documentoUploader.error.invalidExtension"));
			return false;
		}
		if ((file.getSize() / 1024F) > extensaoArquivo.getTamanho()) {
			FacesMessages.instance().add(StatusMessage.Severity.ERROR,
					format(infoxMessages.get("documentoUploader.error.invalidFileSize"), extensaoArquivo.getTamanho()));
			return false;
		}
		if (extensaoArquivo.getPaginavel()) {
			if (validaLimitePorPagina(extensaoArquivo.getTamanhoPorPagina(), dadosArquivo, file.getSize())) {
				return true;
			} else {
				FacesMessages.instance().add(StatusMessage.Severity.ERROR,
						infoxMessages.get("documentoUploader.error.notPaginable"));
				return false;
			}
		}
		return true;
	}

	//Alteração solicitada no bug #69320 para utilizar a média do tamanho do documento pelo número de páginas e comparar com o limite do tamanho por paǵina
	//Isso duplica um método do DocumentoUploaderService
	private boolean validaLimitePorPagina(Integer limitePorPagina, byte[] pdf, Long tamanhoTotalArquivo) {
		PdfReader reader;
		try {
			reader = new PdfReader(pdf);
			int qtdPaginas = reader.getNumberOfPages();
        	long tamanhoPorPagina =  tamanhoTotalArquivo / qtdPaginas;
			if ((tamanhoPorPagina / 1024F) > limitePorPagina) {
				return false;
			}
		} catch (IOException e) {
			LOG.error("Não foi possível recuperar as páginas do arquivo", e);
			return false;
		}
		return true;
	}

	public ClassificacaoDocumento getClassificacaoDocumento() {
		return classificacaoDocumento;
	}

	public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
		this.classificacaoDocumento = classificacaoDocumento;
		getDocumento().setClassificacaoDocumento(classificacaoDocumento);
		clearUploadFile();
	}

	@Override
	public void clear() {
		super.clear();
		setClassificacaoDocumento(null);
		setPasta(null);
	}

	public void podeRenderizar(AjaxBehaviorEvent ajaxBehaviorEvent) {
		UIInput input = (UIInput) ajaxBehaviorEvent.getComponent();
		UIInput input2 = null;
		UIComponent form = input.getParent();
		boolean forcar = false;
		while (!(form instanceof UIForm)) {
			form = form.getParent();
		}
		if (input.getClientId().endsWith(NOME_DOCUMENTO)) {

			input2 = (UIInput) form.findComponent(CLASSIFICACAO_DOCUMENTO_DECORATION)
					.findComponent(CLASSIFICACAO_DOCUMENTO);
		} else {
			UIComponent component = form.findComponent(NOME_DOCUMENTO_DECORATION);
			if (component == null) {
				forcar = true;
			} else {
				input2 = (UIInput) component.findComponent(NOME_DOCUMENTO);
			}
		}
		if (input.getValue() != null && (forcar || (input2.getValue() != null))) {
			Collection<String> ids = FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds();
			ids.add(input.getClientId());
			if (input2 != null) {
				ids.add(input2.getClientId());
			}
			ids.add(form.findComponent(FILE_UPLOAD).getClientId());
		}
	}

}