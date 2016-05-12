package br.com.infox.epp.processo.documento.service;

import static java.text.MessageFormat.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ValidationException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.richfaces.model.UploadedFile;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
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
@Transactional
public class DocumentoUploaderService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "documentoUploaderService";
	private static final LogProvider LOG = Logging.getLogProvider(DocumentoUploaderService.class);
	
	@Inject
	private ExtensaoArquivoManager extensaoArquivoManager;
	@Inject
	private DocumentoManager documentoManager;
	
	public DocumentoBin createProcessoDocumentoBin(UploadedFile uploadedFile) throws Exception {
		DocumentoBin documentoBin = new DocumentoBin();
		documentoBin.setExtensao(getFileType(uploadedFile.getName()));
		documentoBin.setNomeArquivo(uploadedFile.getName());
		documentoBin.setSize(Long.valueOf(uploadedFile.getSize()).intValue());
		documentoBin.setProcessoDocumento(uploadedFile.getData());
		documentoBin.setModeloDocumento(null);
		return documentoBin;
	}
	
	public DocumentoBin createDocumentoBin(UploadedFile uploadedFile) {
        DocumentoBin documentoBin = new DocumentoBin();
        documentoBin.setExtensao(getFileType(uploadedFile.getName()));
        documentoBin.setNomeArquivo(uploadedFile.getName());
        documentoBin.setSize(Long.valueOf(uploadedFile.getSize()).intValue());
        documentoBin.setModeloDocumento(null);
        return documentoBin;
    }
	
	private String getFileType(String nomeArquivo) {
        String ret = "";
        if (nomeArquivo != null) {
            ret = nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1);
        }
        return ret.toLowerCase();
    }

	public void validaDocumento(UploadedFile uploadFile, ClassificacaoDocumento classificacaoDocumento, byte[] dataStream) throws Exception {
        if (uploadFile == null) {
        	throw new ValidationException(InfoxMessages.getInstance().get("documentoUploader.error.noFile"));
        }
        String extensao = getFileType(uploadFile.getName());
        ExtensaoArquivo extensaoArquivo = extensaoArquivoManager.getTamanhoMaximo(classificacaoDocumento, extensao);
        if (extensaoArquivo == null) {
        	throw new ValidationException("Arquivo: "+ uploadFile.getName() + " - " + InfoxMessages.getInstance().get("documentoUploader.error.invalidExtension"));
        }
        if ((uploadFile.getSize() / 1024F) > extensaoArquivo.getTamanho()) {
        	throw new ValidationException("Arquivo: "+ uploadFile.getName() + " - " +format(InfoxMessages.getInstance().get("documentoUploader.error.invalidFileSize"), extensaoArquivo.getTamanho()));
        }
        if (extensaoArquivo.getPaginavel()) {
        	try {
        	    if (dataStream == null) {
        	        dataStream = uploadFile.getData();
        	    }
//        		validaLimitePorPagina(extensaoArquivo.getTamanhoPorPagina(), dataStream, uploadFile.getSize());  // Tira média de tamanho do documento por páginas
        		validaLimitePorPagina(extensaoArquivo.getTamanhoPorPagina(), dataStream);
        	} catch(Exception e){
        		throw new Exception("Arquivo: " + uploadFile.getName() + " - " + e.getMessage());
        	}
        }
    }
	
	public void persist(DocumentoUploadBean documentoUploadBean) throws DAOException {
		Documento documento = documentoUploadBean.getDocumento();
        documentoManager.gravarDocumentoNoProcesso(documento);
	}
	
	//Alteração solicitada no bug #69320 para utilizar a média do tamanho do documento pelo número de páginas e comparar com o limite do tamanho por paǵina 
	public void validaLimitePorPagina(Integer limitePorPagina, byte[] dataStream, Long tamanhoTotalArquivo) throws Exception {
        try {
        	PdfReader reader = new PdfReader(dataStream);
        	int qtdPaginas = reader.getNumberOfPages();
        	long tamanhoPorPagina = tamanhoTotalArquivo / qtdPaginas;
			if ((tamanhoPorPagina / 1024F) > limitePorPagina) {
			    throw new ValidationException("O tamanho limite de " + limitePorPagina + "kb por página é excedido.");
			}
        } catch (IOException e) {
            if (e.getMessage().contains("PDF header signature")) {
                throw new Exception("PDF inválido!");
            } else {
                LOG.error("", e);
                throw new Exception(InfoxMessages.getInstance().get("documentoUploader.error.notPaginable"));
            }
        }
    }
	
	public void validaLimitePorPagina(Integer limitePorPagina, byte[] dataStream){
		try {
			PdfReader reader = new PdfReader(dataStream);
			for (int i = 1,numberOfPages = reader.getNumberOfPages(); i <= numberOfPages; i++) {
				Document document = new Document(reader.getPageSizeWithRotation(i));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PdfCopy writer = new PdfCopy(document, baos);
				PdfImportedPage page = writer.getImportedPage(reader, i);
				document.open();
				writer.addPage(page);
				document.close();
				if (baos.toByteArray().length/1024F > limitePorPagina){
					throw new ValidationException("O tamanho limite de " + limitePorPagina + "kb por página é excedido na página "+ i + ".");
				}
			}
		} catch (DocumentException|IOException e) {
			if (e.getMessage().contains("PDF header signature")) {
                throw new RuntimeException("PDF inválido!");
            } else {
                LOG.error("", e);
                throw new RuntimeException(InfoxMessages.getInstance().get("documentoUploader.error.notPaginable"));
            }
		}
	}
	
}