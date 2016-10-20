package br.com.infox.epp.processo.documento.service;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ValidationException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.model.UploadedFile;

import com.lowagie.text.pdf.PdfReader;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.FileUtil;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ExtensaoArquivo;
import br.com.infox.epp.documento.manager.ExtensaoArquivoManager;
import br.com.infox.epp.processo.documento.anexos.DocumentoUploadBean;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@AutoCreate
@Stateless
@Scope(ScopeType.STATELESS)
@Name(DocumentoUploaderService.NAME)
public class DocumentoUploaderService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "documentoUploaderService";
	private static final LogProvider LOG = Logging.getLogProvider(DocumentoUploaderService.class);
	
	@Inject
	private ExtensaoArquivoManager extensaoArquivoManager;
	@Inject
	private DocumentoManager documentoManager;
	@Inject
	private InfoxMessages infoxMessages;
	
	public DocumentoBin createProcessoDocumentoBin(UploadedFile uploadedFile) throws Exception {
		DocumentoBin documentoBin = new DocumentoBin();
		documentoBin.setExtensao(FileUtil.getFileType(uploadedFile.getName()));
		documentoBin.setNomeArquivo(uploadedFile.getName());
		documentoBin.setSize(Long.valueOf(uploadedFile.getSize()).intValue());
		documentoBin.setProcessoDocumento(uploadedFile.getData());
		documentoBin.setModeloDocumento(null);
		return documentoBin;
	}
	
	public DocumentoBin createDocumentoBin(UploadedFile uploadedFile) {
        DocumentoBin documentoBin = new DocumentoBin();
        documentoBin.setExtensao(FileUtil.getFileType(uploadedFile.getName()));
        documentoBin.setNomeArquivo(uploadedFile.getName());
        documentoBin.setSize(Long.valueOf(uploadedFile.getSize()).intValue());
        documentoBin.setModeloDocumento(null);
        return documentoBin;
    }
	
	
	public void validaDocumento(UploadedFile uploadFile, ClassificacaoDocumento classificacaoDocumento) throws Exception {
		validaDocumento(uploadFile, classificacaoDocumento, null);
	}

	public void validaDocumento(UploadedFile uploadFile, ClassificacaoDocumento classificacaoDocumento, byte[] dataStream) throws Exception {
        if (uploadFile == null) {
        	throw new ValidationException(infoxMessages.get("documentoUploader.error.noFile"));
        }
        String extensao = FileUtil.getFileType(uploadFile.getName());
        ExtensaoArquivo extensaoArquivo = extensaoArquivoManager.getTamanhoMaximo(classificacaoDocumento, extensao);
        if (extensaoArquivo == null) {
        	throw new ValidationException(infoxMessages.get("documentoUploader.error.invalidExtension"));
        }
        if ((uploadFile.getSize() / 1024F) > extensaoArquivo.getTamanho()) {
        	throw new ValidationException(format(infoxMessages.get("documentoUploader.error.invalidFileSize"), extensaoArquivo.getTamanho()));
        }	
        if (extensaoArquivo.getPaginavel()) {
    	    if (dataStream == null) {
    	        dataStream = uploadFile.getData();
    	    }
    		validaLimitePorPagina(extensaoArquivo.getTamanhoPorPagina(), dataStream, uploadFile.getSize());
        }
    }
	
	public void persist(DocumentoUploadBean documentoUploadBean) throws DAOException {
		Documento documento = documentoUploadBean.getDocumento();
        documentoManager.gravarDocumentoNoProcesso(documento);
	}
	
	public void validaLimitePorPagina(Integer limitePorPagina, byte[] dataStream, Long tamanhoTotalArquivo) throws ValidationException {
        try {
        	PdfReader reader = new PdfReader(dataStream);
        	int qtdPaginas = reader.getNumberOfPages();
        	long tamanhoPorPagina = tamanhoTotalArquivo / qtdPaginas;
			if ((tamanhoPorPagina / 1024F) > limitePorPagina) {
			    throw new ValidationException(format(infoxMessages.get("documentoUploader.error.invalidPageSize"), limitePorPagina));
			}
        } catch (IOException e) {
            if (e.getMessage().contains("PDF header signature")) {
                throw new ValidationException(infoxMessages.get("documentoUploader.error.invalidPDFFile"));
            } else {
                LOG.warn("", e);
                throw new ValidationException(infoxMessages.get("documentoUploader.error.notPaginable"));
            }
        }
    }
}