package br.com.infox.epp.fluxo.crud;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.CategoriaItem;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.manager.CategoriaItemManager;
import br.com.infox.epp.fluxo.manager.ItemManager;
import br.com.infox.epp.fluxo.tree.ItemTreeHandler;
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
    	List<CategoriaItem> list = categoriaItemManager.createCategoriaItemList(getInstance(), itemManager.getFolhas(itemASerAdicionado));
	    getInstance().getCategoriaItemList().addAll(list);
        limparTreeDeItem();
    }

    private void limparTreeDeItem() {
        ItemTreeHandler ite = ComponentUtil.getComponent(ItemTreeHandler.NAME);
        ite.clearTree();
    }
    
    public void removeCategoriaItem(CategoriaItem categoriaItem){
        getInstance().getCategoriaItemList().remove(categoriaItem);
        remove(categoriaItem);
        super.update();
    }
}
