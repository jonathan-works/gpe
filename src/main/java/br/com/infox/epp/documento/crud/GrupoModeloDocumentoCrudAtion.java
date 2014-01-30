package br.com.infox.epp.documento.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;

@Name(GrupoModeloDocumentoCrudAtion.NAME)
@Scope(ScopeType.PAGE)
public class GrupoModeloDocumentoCrudAtion extends AbstractCrudAction<GrupoModeloDocumento> {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static final String NAME = "grupoModeloDocumentoCrudAtion";

}
