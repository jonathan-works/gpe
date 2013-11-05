package br.com.infox.epp.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.CategoriaItemDAO;
import br.com.infox.epp.entity.CategoriaItem;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.home.CategoriaHome;
import br.com.infox.ibpm.entity.Item;
import br.com.itx.util.ComponentUtil;

@Name(CategoriaItemManager.NAME)
@AutoCreate
public class CategoriaItemManager extends GenericManager{

	private static final long serialVersionUID = -3580636874720809514L;

	public static final String NAME = "categoriaItemManager";

	@In
	private CategoriaItemDAO categoriaItemDAO;
	
	public List<CategoriaItem> listByCategoria(Categoria categoria) {
		return categoriaItemDAO.listByCategoria(categoria);
	}
	
	public Long countByCategoriaItem(Categoria categoria, Item item) {
	    return categoriaItemDAO.countByCategoriaItem(categoria, item);
	}
	
	public boolean containsCategoriaItem(CategoriaItem categoriaItem)  {
	    return categoriaItemDAO.countByCategoriaItem(categoriaItem.getCategoria(), categoriaItem.getItem()) > 0;
	}
	
	public Categoria getCategoriaAtual() {
	    CategoriaHome categoriaHome = ComponentUtil.getComponent(CategoriaHome.NAME);
	    return categoriaHome.getInstance();
	}
	
	public List<CategoriaItem> createCategoriaItemList(Categoria categoria, Set<Item> itens){
	    List<CategoriaItem> categoriaItemList = new ArrayList<CategoriaItem>();
        if (itens != null) {
            for (Item item : itens) {
                if (item.getAtivo()) {
                    CategoriaItem ci = new CategoriaItem(categoria, item);
                    persist(ci);
                    categoriaItemList.add(ci);
                }
            }
        }
        return categoriaItemList;
	}
	
}