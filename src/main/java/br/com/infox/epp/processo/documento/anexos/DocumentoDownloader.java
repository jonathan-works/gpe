package br.com.infox.epp.processo.documento.anexos;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoManager;
import br.com.infox.epp.processo.documento.sigilo.service.SigiloDocumentoService;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(DocumentoDownloader.NAME)
@Transactional
public class DocumentoDownloader implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final float BYTES_IN_A_KILOBYTE = 1024f;
	public static final String NAME = "documentoDownloader";
	private static final LogProvider LOG = Logging.getLogProvider(DocumentoValidator.class);

    @In
    private DocumentoBinarioManager documentoBinarioManager;
    @In
    private DocumentoManager documentoManager;
    @In
    private SigiloDocumentoManager sigiloDocumentoManager;
    @In
    private SigiloDocumentoService sigiloDocumentoService;
    @In
    private DocumentoBinManager documentoBinManager;


    public void downloadDocumento(Documento documento) {
    	UsuarioLogin usuario = Authenticator.getUsuarioLogado();
    	if (sigiloDocumentoManager.isSigiloso(documento.getId()) && (usuario == null || !sigiloDocumentoService.possuiPermissao(documento, usuario))) {
            FacesMessages.instance().add("Este documento é sigiloso.");
            LOG.warn("Tentativa não autorizada de acesso a documento sigiloso, id: " + documento.getId());
            return;
        }
        downloadDocumento(documento.getDocumentoBin());
    }
    
    public void downloadDocumento(DocumentoBin documento) {
        byte[] data = documentoBinarioManager.getData(documento.getId());
        String fileName = documento.getNomeArquivo();
        String contentType = "application/" + documento.getExtensao();
        if (contentType.equals("application/pdf") && !documento.getAssinaturas().isEmpty()) {
            HttpServletResponse response = FileDownloader.prepareDownloadResponse(contentType, fileName);
            try {
                documentoBinManager.writeMargemDocumento(documento, data, response.getOutputStream());
                FacesContext.getCurrentInstance().responseComplete();
            } catch (IOException | BusinessException e) {
                LOG.error("", e);
                FacesMessages.instance().clear();
                FacesMessages.instance().add("Erro ao gerar a margem do PDF: " + e.getMessage());
            }
        } else {
            FileDownloader.download(data, contentType, fileName);
        }
    }

    /**
     * Recebe o número de bytes e retorna o número em Kb (kilobytes).
     * 
     * @param bytes número em bytes
     * @return número em kilobytes
     */
    public String getFormattedKb(DocumentoBin binario) {
        Integer bytes = binario.getSize();
        if (bytes != null && bytes > 0) {
            NumberFormat formatter = DecimalFormat.getNumberInstance(new Locale("pt", "BR"));
            formatter.setMinimumIntegerDigits(1);
            formatter.setMaximumFractionDigits(2);
            formatter.setMinimumFractionDigits(2);
            float kbytes = bytes / BYTES_IN_A_KILOBYTE;
            return formatter.format(kbytes) + " Kb";
        } else {
            return null;
        }
    }

    public void downloadDocumento(String idDocumento) {
        Documento documento = documentoManager.find(Integer.valueOf(clearId(idDocumento)));
        if (documento != null) {
            downloadDocumento(documento);
        } else {
            LOG.warn("Documento não encontrado, id: " + idDocumento);
        }
    }
    
    // TODO verificar solução melhor para isso
    private String clearId(String id) {
        return id.replaceAll("\\D+", "");
    }
}
