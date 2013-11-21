package br.com.infox.epp.documento.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.TipoProcessoDocumento;

@Name(ClassificacaoDocumentoCrudAction.NAME)
public class ClassificacaoDocumentoCrudAction extends AbstractCrudAction<TipoProcessoDocumento> {
    
    public static final String NAME = "classificacaoDocumentoCrudAction";
    
    @Override
    public String save() {
        if (!getInstance().getNumera()){
            getInstance().setTipoNumeracao(null);
        }
        return super.save();
    }

}
