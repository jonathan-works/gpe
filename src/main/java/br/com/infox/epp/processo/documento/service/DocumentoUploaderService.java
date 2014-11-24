package br.com.infox.epp.processo.documento.service;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.Messages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.richfaces.model.UploadedFile;

import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.file.reader.InfoxPdfReader;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ExtensaoArquivo;
import br.com.infox.epp.documento.manager.ExtensaoArquivoManager;
import br.com.infox.epp.processo.documento.anexos.DocumentoUploadBean;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;

import com.lowagie.text.pdf.PdfReader;

@AutoCreate
@Scope(ScopeType.STATELESS)
@Name(DocumentoUploaderService.NAME)
public class DocumentoUploaderService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "documentoUploaderService";
	private static final LogProvider LOG = Logging.getLogProvider(DocumentoUploaderService.class);
	
	@In
	private ExtensaoArquivoManager extensaoArquivoManager;
	@In
	private DocumentoManager documentoManager;
	@In
	private DocumentoBinarioManager documentoBinarioManager;
	
	public DocumentoBin createProcessoDocumentoBin(UploadedFile uploadedFile) throws Exception {
		DocumentoBin documentoBin = new DocumentoBin();
		documentoBin.setExtensao(getFileType(uploadedFile.getName()));
		documentoBin.setNomeArquivo(uploadedFile.getName());
		documentoBin.setMd5Documento(getMD5(uploadedFile.getData()));
		documentoBin.setSize(Long.valueOf(uploadedFile.getSize()).intValue());
		documentoBin.setProcessoDocumento(uploadedFile.getData());
		documentoBin.setModeloDocumento(null);
		return documentoBin;
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
	
	public void validaDocumento(UploadedFile uploadFile, ClassificacaoDocumento classificacaoDocumento, byte[] dataStream) throws Exception {
        if (uploadFile == null) {
        	throw new Exception(Messages.instance().get("documentoUploader.error.noFile"));
        }
        String extensao = getFileType(uploadFile.getName());
        ExtensaoArquivo extensaoArquivo = extensaoArquivoManager.getTamanhoMaximo(classificacaoDocumento, extensao);
        if (extensaoArquivo == null) {
        	throw new Exception(Messages.instance().get("documentoUploader.error.invalidExtension"));
        }
        if ((uploadFile.getSize() / 1024F) > extensaoArquivo.getTamanho()) {
        	throw new Exception(format(Messages.instance().get("documentoUploader.error.invalidFileSize"), extensaoArquivo.getTamanho()));
        }
        if (extensaoArquivo.getPaginavel()) {
            validaLimitePorPagina(extensaoArquivo.getTamanhoPorPagina(), dataStream);
        }
    }
	
	public void persist(DocumentoUploadBean documentoUploadBean) throws DAOException {
		Documento documento = documentoUploadBean.getDocumento();
		String texto = InfoxPdfReader.readPdfFromByteArray(documentoUploadBean.getData());
        documentoManager.gravarDocumentoNoProcesso(documento.getProcesso(), documento);
        documento.getDocumentoBin().setModeloDocumento(texto);
        documentoBinarioManager.salvarBinario(documento.getDocumentoBin().getId(), documento.getDocumentoBin().getProcessoDocumento());
	}
	
	private void validaLimitePorPagina(Integer limitePorPagina, byte[] dataStream) throws Exception {
        try {
        	PdfReader reader = new PdfReader(dataStream);
            int qtdPaginas = reader.getNumberOfPages();
            for (int i = 1; i <= qtdPaginas; i++) {
                if ((reader.getPageContent(i).length / 1024F) > limitePorPagina) {
                    throw new Exception("Arquivo excede o limite por página");
                }
            }
        } catch (IOException e) {
            LOG.error("Não foi possível recuperar as páginas do arquivo", e);
            throw new Exception(Messages.instance().get("documentoUploader.error.notPaginable"));
        }
    }
	
}
