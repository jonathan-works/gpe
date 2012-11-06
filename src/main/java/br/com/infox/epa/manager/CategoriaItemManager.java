package br.com.infox.epa.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epa.dao.CategoriaItemDAO;
import br.com.infox.epa.entity.Categoria;
import br.com.infox.epa.entity.CategoriaItem;

@Name(CategoriaItemManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class CategoriaItemManager extends GenericManager{

	private static final long serialVersionUID = -3580636874720809514L;

	public static final String NAME = "categoriaItemManager";

	@In
	private CategoriaItemDAO categoriaItemDAO;
	
	public List<CategoriaItem> listByCategoria(Categoria categoria) {
		return categoriaItemDAO.listByCategoria(categoria);
	}
	
}