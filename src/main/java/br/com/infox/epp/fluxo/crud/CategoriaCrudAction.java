package br.com.infox.epp.fluxo.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.CategoriaItem;
import br.com.infox.epp.fluxo.manager.CategoriaItemManager;
import br.com.infox.epp.manager.ItemManager;
import br.com.infox.ibpm.component.tree.ItemTreeHandler;
import br.com.infox.ibpm.entity.Item;
import br.com.itx.util.ComponentUtil;

@Name(CategoriaCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class CategoriaCrudAction extends AbstractCrudAction<Categoria> {
    
    public static final String NAME = "categoriaCrudAction";
    
    @In private ItemManager itemManager;
    @In private CategoriaItemManager categoriaItemManager;
    
    private Item itemASerAdicionado;
    
    public Item getItemASerAdicionado() {
        return itemASerAdicionado;
    }

    public void setItemASerAdicionado(Item itemASerAdicionado) {
        this.itemASerAdicionado = itemASerAdicionado;
    }
    
    public void addCategoriaItem(){
        getInstance().getCategoriaItemList().addAll(
                categoriaItemManager.createCategoriaItemList(
                        getInstance(), itemManager.getFolhas(itemASerAdicionado)));
        super.update();
        limparTreeDeItem();
    }

    private void limparTreeDeItem() {
        ItemTreeHandler ite = ComponentUtil.getComponent("itemTree");
        ite.clearTree();
    }
    
    public void removeCategoriaItem(CategoriaItem categoriaItem){
        getInstance().getCategoriaItemList().remove(categoriaItem);
        remove(categoriaItem);
        super.update();
    }
}
