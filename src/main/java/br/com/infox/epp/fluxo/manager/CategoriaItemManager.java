package br.com.infox.epp.fluxo.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.fluxo.dao.CategoriaItemDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.CategoriaItem;
import br.com.infox.epp.fluxo.entity.Item;

@Name(CategoriaItemManager.NAME)
@AutoCreate
public class CategoriaItemManager extends Manager<CategoriaItemDAO, CategoriaItem> {

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

    public boolean containsCategoriaItem(CategoriaItem categoriaItem) {
        return categoriaItemDAO.countByCategoriaItem(categoriaItem.getCategoria(), categoriaItem.getItem()) > 0;
    }

}
