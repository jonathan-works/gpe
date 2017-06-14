package br.com.infox.epp.fluxo.crud;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.list.CategoriaItemList;
import br.com.infox.epp.fluxo.manager.CategoriaManager;

@Named
@ViewScoped
public class CategoriaCrudAction extends AbstractCrudAction<Categoria, CategoriaManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private CategoriaManager categoriaManager;
    @Inject
    private CategoriaItemCrudAction categoriaItemCrudAction;
    @Inject
    private CategoriaItemList categoriaItemList;

    public void onClickCategoriaItemTab() {
        categoriaItemCrudAction.setCategoria(getInstance());
        categoriaItemList.getEntity().setCategoria(getInstance());
    }

    @Override
    protected CategoriaManager getManager() {
        return categoriaManager;
    }
}
