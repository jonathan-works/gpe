package br.com.infox.epp.processo.documento.anexos;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.documento.dao.DocumentoDAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.hibernate.util.HibernateUtil;

@ViewScoped
@Stateful
@Named(DocumentoHtmlView.NAME)
public class DocumentoHtmlView implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_VISUALIZACAO = "/Painel/documentoHTML.seam";

    public static final String NAME = "documentoHtmlView";

    @Inject
    private DocumentoManager documentoManager;
    @Inject
    private DocumentoBinManager documentoBinManager;
    @Inject
	private DocumentoDAO documentoDAO;

    private Documento viewInstance;
    private DocumentoBin documentoBin;

    public void setIdDocumento(String idDocumento) {
        if (idDocumento != null && !"".equals(idDocumento)) {
            Integer idInteger = Integer.parseInt(clearId(idDocumento));
            if (idInteger != null && idInteger != 0) {
                setViewInstance(documentoManager.find(idInteger));
            }
        }
    }
    
    public void setIdDocumentoBin(String idDocumentoBin) {
    	if (idDocumentoBin != null && !"".equals(idDocumentoBin)) {
    	    Integer idInteger = Integer.parseInt(clearId(idDocumentoBin));
    	    if (idInteger != null && idInteger != 0) {
    	        setViewInstanceBin(documentoBinManager.find(idInteger));
    	    }
    	}
    }

    public String setViewInstanceBin(DocumentoBin documentoBin) {
        this.documentoBin = (DocumentoBin) HibernateUtil.removeProxy(documentoBin);
        return PAGINA_VISUALIZACAO;
    }
    
    public String setViewInstance(Documento documento) {
        viewInstance = documento;
        this.documentoBin = viewInstance.getDocumentoBin();
        return PAGINA_VISUALIZACAO;
    }

    public String getUuidText(){
        return documentoBinManager.getTextoCodigo(documentoBin.getUuid(), documentoDAO.getDocumentosFromDocumentoBin(documentoBin).get(0).getExcluido());
    }
    
    public String getSignatureText(){
        return documentoBinManager.getTextoAssinatura(documentoBin);
    }
    
    public String getQrCode(){
        byte[] qrCode = documentoBinManager.getQrCodeSignatureImage(documentoBin);
        StringBuilder sb = new StringBuilder();
        sb.append("data:image/png;base64,");
        try {
            sb.append(StringUtils.toString(org.apache.commons.codec.binary.Base64.encodeBase64(qrCode, true), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return "";
        }
        System.out.println(sb.toString());
        return sb.toString();
    }
    
    public String getConteudo() {
        return this.documentoBin.getModeloDocumento();
    }

    // TODO verificar solução melhor para isso
    private String clearId(String id) {
        return id.replaceAll("\\D+", "");
    }
}
