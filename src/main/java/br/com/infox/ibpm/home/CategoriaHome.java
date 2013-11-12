package br.com.infox.ibpm.home;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.entity.Categoria;
import br.com.infox.epp.entity.CategoriaItem;
import br.com.infox.epp.manager.CategoriaItemManager;
import br.com.infox.epp.manager.ItemManager;
import br.com.infox.ibpm.entity.Item;
import br.com.itx.component.AbstractHome;

/**
 * 
 * @author Daniel
 *
 */
@Name(CategoriaHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class CategoriaHome extends AbstractHome<Categoria> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "categoriaHome";
	
	@In private ItemManager itemManager;
	@In private CategoriaItemManager categoriaItemManager;
	
	private Item itemASerAdicionado;
	
	public void removeCategoriaItem(CategoriaItem categoriaItem){
	    getInstance().getCategoriaItemList().remove(categoriaItem);
	    categoriaItemManager.remove(categoriaItem);
	    getEntityManager().refresh(getInstance());
	}
	
	public void addCategoriaItem(Item item){
	    List<CategoriaItem> list = categoriaItemManager.createCategoriaItemList(instance, itemManager.getFolhas(item));
	    getInstance().getCategoriaItemList().addAll(list);
	}

    public Item getItemASerAdicionado() {
        return itemASerAdicionado;
    }

    public void setItemASerAdicionado(Item itemASerAdicionado) {
        this.itemASerAdicionado = itemASerAdicionado;
    }
	
}