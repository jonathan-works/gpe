package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.epp.entity.Categoria;
import br.com.infox.epp.entity.CategoriaItem;
import br.com.infox.epp.list.CategoriaList;
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
	
	public static final String TEMPLATE = "/Categoria/CategoriaTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "Categoria.xls";
	
	@In private ItemManager itemManager;
	@In private CategoriaItemManager categoriaItemManager;
	
	private Item itemASerAdicionado;
	
	@Override
	public EntityList<Categoria> getBeanList() {
		return CategoriaList.instance();
	}
	
	@Override
	public String getTemplate() {
		return TEMPLATE;
	}
	
	@Override
	public String getDownloadXlsName() {
		return DOWNLOAD_XLS_NAME;
	}
	
	public void removeCategoriaItem(CategoriaItem categoriaItem){
	    
	    instance.getCategoriaItemList().remove(categoriaItem);
	    getEntityManager().remove(categoriaItem);
	    super.update();
	}
	
	public void addCategoriaItem(Item item){
	    instance.getCategoriaItemList().addAll(categoriaItemManager.createCategoriaItemList(instance, itemManager.getFolhas(item)));
	    super.update();
	}

    public Item getItemASerAdicionado() {
        return itemASerAdicionado;
    }

    public void setItemASerAdicionado(Item itemASerAdicionado) {
        this.itemASerAdicionado = itemASerAdicionado;
    }
	
}