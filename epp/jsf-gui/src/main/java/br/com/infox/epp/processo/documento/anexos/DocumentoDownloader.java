package br.com.infox.epp.processo.documento.anexos;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.manager.StatusProcessoSearch;
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
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.sigilo.manager.SigiloDocumentoManager;
import br.com.infox.epp.processo.documento.sigilo.service.SigiloDocumentoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.path.PathResolver;

@AutoCreate
@Name(DocumentoDownloader.NAME)
@Scope(ScopeType.STATELESS)
@Stateless
@Transactional
public class DocumentoDownloader implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final float BYTES_IN_A_KILOBYTE = 1024f;
	public static final String NAME = "documentoDownloader";
	private static final LogProvider LOG = Logging.getLogProvider(DocumentoValidator.class);

    @In
    private PathResolver pathResolver;
    @In
    private DocumentoBinarioManager documentoBinarioManager;
    @In
    private DocumentoManager documentoManager;
    @In
    private SigiloDocumentoManager sigiloDocumentoManager;
    @Inject
    private SigiloDocumentoService sigiloDocumentoService;
    @In
    private DocumentoBinManager documentoBinManager;
    @Inject
	private ProcessoManager processoManager;
    
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
    	        getFileDownloader().downloadDocumento(documento);
            } catch (IOException e) {
                getActionMessagesService().handleException(null, e);
            }
    	}
    }
    
    public void downloadDocumento(Documento documento) {
    	downloadDocumento(documento, true);
    }
    
    public void downloadDocumento(DocumentoBin documento, boolean gerarMargens) {
        try {
            getFileDownloader().downloadDocumento(documento);
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

    @Inject
    private StatusProcessoSearch statusProcessoSearch;

    private static final String statusProcessoArquivado = "Processo Arquivado";

    public void downloadDocumentoResumoProcesso(Integer idProcesso) {
    	Processo processo = processoManager.find(idProcesso);
        DocumentoBin documentoBin = documentoBinManager.createDocumentoBinResumoDocumentosProcesso(processo);
    	downloadDocumento(documentoBin);
    }
    
    public void previewDocumento(byte[] data, String fileName) {
        try {
            getFileDownloader().downloadDocumento(data, "application/pdf", fileName);
        } catch (IOException e) {
            getActionMessagesService().handleException(null, e);
        }
    }

	public void downloadPdf(Documento documento, byte[] pdf, String nome) {
    	if (validarSigilo(documento)) {
    	    try {
    	        getFileDownloader().downloadDocumento(documento);
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
        return Beans.getReference(FileDownloader.class);
    }
    
    private ActionMessagesService getActionMessagesService(){
        return Beans.getReference(ActionMessagesService.class);
    }
    
    public void limparFlash() {
        System.out.println("limpa");
    }
    
}
