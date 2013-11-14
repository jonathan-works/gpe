package br.com.infox.epp.documento.crud;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;
import br.com.infox.epp.documento.entity.TipoProcessoDocumentoPapel;

@Name(ClassificacaoDocumentoPapelCrudAction.NAME)
public class ClassificacaoDocumentoPapelCrudAction extends AbstractCrudAction<TipoProcessoDocumentoPapel> {
    
    public static final String NAME = "classificacaoDocumentoPapelCrudAction";
    
    @In private PapelManager papelManager;
    
    public void setTipoProcessoDocumento(TipoProcessoDocumento tipoProcessoDocumento) {
        newInstance();
        getInstance().setTipoProcessoDocumento(tipoProcessoDocumento);
    }
    
    public List<Papel> papelItems() {
        return papelManager.getPapeisNaoAssociadosATipoProcessoDocumento(getInstance().getTipoProcessoDocumento());
    }

}
