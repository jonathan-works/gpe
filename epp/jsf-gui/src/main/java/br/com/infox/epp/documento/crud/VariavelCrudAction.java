package br.com.infox.epp.documento.crud;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.Variavel;
import br.com.infox.epp.documento.list.associated.AssociatedTipoModeloVariavelList;
import br.com.infox.epp.documento.list.associative.AssociativeTipoModeloDocumentoList;
import br.com.infox.epp.documento.manager.VariavelManager;

@Named
@ViewScoped
public class VariavelCrudAction extends AbstractCrudAction<Variavel, VariavelManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private VariavelManager variavelManager;

    @Inject
    private AssociatedTipoModeloVariavelList associatedTipoModeloVariavelList;
    @Inject
    private AssociativeTipoModeloDocumentoList associativeTipoModeloDocumentoList;
    @Inject
    private VariavelTipoModeloDocumentoCrudAction variavelTipoModeloDocumentoCrudAction;

    public void onClickTipoModeloTab() {
        associatedTipoModeloVariavelList.getEntity().setVariavel(getInstance());
        associativeTipoModeloDocumentoList.setVariavelToIgnore(getInstance().getVariavel());
        variavelTipoModeloDocumentoCrudAction.setVariavelAtual(getInstance());
    }

    @Override
    protected VariavelManager getManager() {
        return variavelManager;
    }
}
