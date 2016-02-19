package br.com.infox.epp.processo.documento.service;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.international.Messages;
import org.richfaces.model.UploadedFile;

import com.lowagie.text.pdf.PdfReader;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ExtensaoArquivo;
import br.com.infox.epp.documento.manager.ExtensaoArquivoManager;
import br.com.infox.epp.processo.documento.anexos.DocumentoUploadBean;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@AutoCreate
@Scope(ScopeType.STATELESS)
@Name(DocumentoUploaderService.NAME)
@Transactional
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
        documentoManager.gravarDocumentoNoProcesso(documento.getProcesso(), documento);
	}
	
	private void validaLimitePorPagina(Integer limitePorPagina, byte[] dataStream) throws Exception {
        try {
        	PdfReader reader = new PdfReader(dataStream);
        	int qtdPaginas = reader.getNumberOfPages();
        	float fileLength = reader.getFileLength() / 1024F;
            float averagePage = fileLength / qtdPaginas;
            if (averagePage > limitePorPagina) {
            	throw new Exception(InfoxMessages.getInstance().get("documentoUploader.error.notPaginable"));
            }
            for (int i = 1; i <= qtdPaginas; i++) {
				if ((reader.getPageContent(i).length / 1024F) > limitePorPagina) {
					throw new Exception(InfoxMessages.getInstance().get("documentoUploader.error.notPaginable"));
				}
			}
        } catch (IOException e) {
            LOG.error("Não foi possível recuperar as páginas do arquivo", e);
            throw new Exception(InfoxMessages.getInstance().get("documentoUploader.error.notPaginable"));
        }
    }
	
}