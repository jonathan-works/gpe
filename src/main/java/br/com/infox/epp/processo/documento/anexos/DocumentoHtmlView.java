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

    public void setIdDocumento(Integer idDocumento) {
        if (idDocumento != null && idDocumento != 0) {
            setViewInstance(documentoManager.find(idDocumento));
        }
    }
    
    public void setIdDocumentoBin(Integer idDocumentoBin) {
    	if (idDocumentoBin != null && idDocumentoBin != 0) {
    		setViewInstanceBin(documentoBinManager.find(idDocumentoBin));
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

}
