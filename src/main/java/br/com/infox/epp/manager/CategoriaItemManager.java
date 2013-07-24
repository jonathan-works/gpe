package br.com.infox.epp.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.CategoriaItemDAO;
import br.com.infox.epp.entity.Categoria;
import br.com.infox.epp.entity.CategoriaItem;
import br.com.infox.ibpm.entity.Item;
import br.com.infox.ibpm.home.CategoriaHome;
import br.com.itx.util.ComponentUtil;

@Name(CategoriaItemManager.NAME)
@Scope(ScopeType.EVENT)
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
	
}