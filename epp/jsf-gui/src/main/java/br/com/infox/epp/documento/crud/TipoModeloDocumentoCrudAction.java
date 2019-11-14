package br.com.infox.epp.documento.crud;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.list.TipoModeloDocumentoPapelList;
import br.com.infox.epp.documento.list.associated.AssociatedVariavelTipoModeloList;
import br.com.infox.epp.documento.list.associative.AssociativeVariavelList;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.manager.TipoModeloDocumentoManager;

@Named
@ViewScoped
public class TipoModeloDocumentoCrudAction extends AbstractCrudAction<TipoModeloDocumento, TipoModeloDocumentoManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ModeloDocumentoManager modeloDocumentoManager;
    @Inject
    private TipoModeloDocumentoManager tipoModeloDocumentoManager;

    @Inject
    private AssociatedVariavelTipoModeloList associatedVariavelTipoModeloList;
    @Inject
    private AssociativeVariavelList associativeVariavelList;
    @Inject
    private TipoModeloDocumentoVariavelCrudAction tipoModeloDocumentoVariavelCrudAction;
    @Inject
    private TipoModeloDocumentoPapelCrudAction tipoModeloDocumentoPapelCrudAction;
    @Inject
    private TipoModeloDocumentoPapelList tipoModeloDocumentoPapelList;

    public List<ModeloDocumento> getListaDeModeloDocumento() {
        final TipoModeloDocumento tipoModeloDocumento = getInstance();
        if (isManaged()) {
            return modeloDocumentoManager.getModeloDocumentoByGrupoAndTipo(tipoModeloDocumento.getGrupoModeloDocumento(), tipoModeloDocumento);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected TipoModeloDocumentoManager getManager() {
        return tipoModeloDocumentoManager;
    }

    public void onClickVariaveisTab() {
        associatedVariavelTipoModeloList.getEntity().setTipoModeloDocumento(getInstance());
        tipoModeloDocumentoVariavelCrudAction.setTipoModeloDocumentoAtual(getInstance());
        associativeVariavelList.setTipoModeloToIgnore(getInstance());
        associativeVariavelList.onClickVariaveisTab(getInstance());
    }

    public void onClickPapeisTab() {
        tipoModeloDocumentoPapelCrudAction.setTipoModeloDocumentoAtual(getInstance());
        tipoModeloDocumentoPapelList.getEntity().setTipoModeloDocumento(getInstance());
    }
}
