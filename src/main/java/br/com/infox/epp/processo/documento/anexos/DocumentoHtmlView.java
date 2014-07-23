package br.com.infox.epp.processo.documento.anexos;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;

@Name(DocumentoHtmlView.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class DocumentoHtmlView {

    private static final String PAGINA_VISUALIZACAO = "/Painel/documentoHTML.seam";

    public static final String NAME = "documentoHtmlView";

    @In
    private ProcessoDocumentoManager processoDocumentoManager;

    private ProcessoDocumento viewInstance;
    private ProcessoDocumentoBin processoDocumentoBin;

    public void setIdDocumento(Integer idDocumento) {
        if (idDocumento != null && idDocumento != 0) {
            setViewInstance(processoDocumentoManager.find(idDocumento));
        }
    }

    public String setViewInstanceBin(ProcessoDocumentoBin processoDocumentoBin) {
        this.processoDocumentoBin = processoDocumentoBin;
        return PAGINA_VISUALIZACAO;
    }
    
    public String setViewInstance(ProcessoDocumento processoDocumento) {
        viewInstance = processoDocumento;
        this.processoDocumentoBin = viewInstance.getProcessoDocumentoBin();
        return PAGINA_VISUALIZACAO;
    }

    public String getConteudo() {
        return this.processoDocumentoBin.getModeloDocumento();
    }

}
