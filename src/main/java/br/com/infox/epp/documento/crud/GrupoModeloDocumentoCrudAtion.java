package br.com.infox.epp.documento.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;

@Name(GrupoModeloDocumentoCrudAtion.NAME)
@Scope(ScopeType.PAGE)
public class GrupoModeloDocumentoCrudAtion extends AbstractCrudAction<GrupoModeloDocumento> {
    
    public static final String NAME = "grupoModeloDocumentoCrudAtion";

}
