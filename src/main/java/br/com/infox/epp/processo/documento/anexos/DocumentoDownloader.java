package br.com.infox.epp.processo.documento.anexos;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.ejb.Stateless;
import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.config.BeanManager;
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
import br.com.infox.seam.path.PathResolver;

@AutoCreate
@Scope(ScopeType.EVENT)
@Name(DocumentoDownloader.NAME)
@Stateless
@Transactional
public class DocumentoDownloader implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final float BYTES_IN_A_KILOBYTE = 1024f;
	public static final String NAME = "documentoDownloader";
	private static final LogProvider LOG = Logging.getLogProvider(DocumentoValidator.class);

    private static final String URL_DOWNLOAD = "{0}/downloadDocumento.seam?id={1}";

    @In
    private PathResolver pathResolver;
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
    
    private String mensagemErro;
    
    /**
     * Define se ocorreu algum erro ao gerar as margens contendo informações de assinatura de um PDF
     */
    private boolean erroMargem;
    
    public boolean isErroMargem() {
		return erroMargem;
	}

	public void downloadDocumento(Integer idDocumento) {
        Documento documento = documentoManager.find(idDocumento);
        downloadDocumento(documento);
    }

    public void downloadDocumento(Documento documento, boolean gerarMargens) {
    	if (validarSigilo(documento)) {
    	    try {
    	        getFileDownloader().downloadDocumentoViaServlet(documento);
            } catch (IOException e) {
                getActionMessagesService().handleException(null, e);
            }
    	}
    }
    
    public void downloadDocumento(Documento documento) {
    	downloadDocumento(documento, true);
    }
    
    public void downloadDocumentoFlash(Integer idDocumento) {
    	Documento documento = documentoManager.find(idDocumento);
    	downloadDocumentoFlash(documento.getDocumentoBin());
    }
    
    //FIXME: Função adicionada somente para remover a variável 'documentoOriginal' do escopo flash que fica com o mesmo valor em vários requests. Remover quando achar uma forma alternativa
    public void downloadDocumentoFlash(DocumentoBin documento) {
    	Boolean documentoOriginal = (Boolean)FacesContext.getCurrentInstance().getExternalContext().getFlash().get("documentoOriginal");
    	if(documentoOriginal == null)
    		documentoOriginal = Boolean.FALSE;
    	
    	FacesContext.getCurrentInstance().getExternalContext().getFlash().put("documentoOriginal", false);
    	downloadDocumento(documento, !documentoOriginal);
    }

    public void downloadDocumento(DocumentoBin documento, boolean gerarMargens) {
        try {
            getFileDownloader().downloadDocumentoViaServlet(documento);
        } catch (IOException e) {
            getActionMessagesService().handleException(null, e);
        }
    }
    
    public void downloadDocumento(DocumentoBin documento) {
    	downloadDocumento(documento, true);
    }
    
    public void downloadDocumentoBin(Integer idDocumentoBin) {
    	DocumentoBin documentoBin = documentoBinManager.find(idDocumentoBin);
    	downloadDocumento(documentoBin);
    }

	public void downloadPdf(Documento documento, byte[] pdf, String nome) {
    	if (validarSigilo(documento)) {
    	    try {
    	        getFileDownloader().downloadDocumentoViaServlet(documento);
            } catch (IOException e) {
                getActionMessagesService().handleException(null, e);
            }
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

    public void downloadDocumento(String idDocumento, boolean binario) {
    	downloadDocumento(Integer.parseInt(idDocumento), binario);
    }
    
    public void downloadDocumento(Integer idDocumento, boolean binario) {
    	if(binario) {
    		downloadDocumentoBin(idDocumento);
    		return;
    	}
        Documento documento = documentoManager.find(idDocumento);
        if (documento != null) {
            downloadDocumento(documento);
        } else {
            LOG.warn("Documento não encontrado, id: " + idDocumento);
        }
    	
    }
    public void downloadDocumento(String idDocumento) {
    	downloadDocumento(Integer.valueOf(clearId(idDocumento)), false);
    }

    public String getViewUrl(Documento documento) {
    	if(documento == null){
    		throw new BusinessException("Não foi possível carregar os documentos. ");
    	}
    		
        return MessageFormat.format(URL_DOWNLOAD, pathResolver.getContextPath(), documento.getId().toString());
    }
    
    public String getMensagemErro() {
		return mensagemErro;
	}

    // TODO verificar solução melhor para isso
    private String clearId(String id) {
        return id.replaceAll("\\D+", "");
    }
    
    private boolean validarSigilo(Documento documento) {
		UsuarioLogin usuario = Authenticator.getUsuarioLogado();
    	if (sigiloDocumentoManager.isSigiloso(documento.getId()) && (usuario == null || !sigiloDocumentoService.possuiPermissao(documento, usuario))) {
            FacesMessages.instance().add("Este documento é sigiloso.");
            LOG.warn("Tentativa não autorizada de acesso a documento sigiloso, id: " + documento.getId());
            return false;
        }
    	return true;
	}
    
    private FileDownloader getFileDownloader(){
        return BeanManager.INSTANCE.getReference(FileDownloader.class);
    }
    
    private ActionMessagesService getActionMessagesService(){
        return BeanManager.INSTANCE.getReference(ActionMessagesService.class);
    }
    
}
