package br.com.infox.epp.processo.documento.anexos;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;

@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name(DocumentoHtmlView.NAME)
public class DocumentoHtmlView implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_VISUALIZACAO = "/Painel/documentoHTML.seam";

    public static final String NAME = "documentoHtmlView";

    @In
    private DocumentoManager documentoManager;
    @In
    private DocumentoBinManager documentoBinManager;

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
        this.documentoBin = documentoBin;
        return PAGINA_VISUALIZACAO;
    }
    
    public String setViewInstance(Documento documento) {
        viewInstance = documento;
        this.documentoBin = viewInstance.getDocumentoBin();
        return PAGINA_VISUALIZACAO;
    }

    public String getConteudo() {
        return this.documentoBin.getModeloDocumento();
    }

    // TODO verificar solução melhor para isso
    private String clearId(String id) {
        return id.replaceAll("\\D+", "");
    }
}
