package br.com.infox.epp.documento.crud;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;
import br.com.infox.epp.documento.manager.GrupoModeloDocumentoManager;

@Named
@ViewScoped
public class GrupoModeloDocumentoCrudAtion extends AbstractCrudAction<GrupoModeloDocumento, GrupoModeloDocumentoManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private GrupoModeloDocumentoManager grupoModeloDocumentoManager;

    @Override
    protected GrupoModeloDocumentoManager getManager() {
        return grupoModeloDocumentoManager;
    }
}
