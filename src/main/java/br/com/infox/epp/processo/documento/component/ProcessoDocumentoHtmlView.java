package br.com.infox.epp.processo.documento.component;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;

@Name(ProcessoDocumentoHtmlView.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessoDocumentoHtmlView {
    
    public static final String NAME = "processoDocumentoHtmlView";
    
    private ProcessoDocumento viewInstance;
    
    public String setViewInstance(ProcessoDocumento processoDocumento) {
        viewInstance = processoDocumento;
        return "/Painel/documentoHTML.seam";
    }
    
    public String getConteudo(){
        return viewInstance.getProcessoDocumentoBin().getModeloDocumento();
    }

}
