package br.com.infox.epp.documento.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;
import br.com.infox.epp.documento.manager.GrupoModeloDocumentoManager;

@Name(GrupoModeloDocumentoCrudAtion.NAME)
@Scope(ScopeType.CONVERSATION)
public class GrupoModeloDocumentoCrudAtion extends AbstractCrudAction<GrupoModeloDocumento, GrupoModeloDocumentoManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "grupoModeloDocumentoCrudAtion";

}
