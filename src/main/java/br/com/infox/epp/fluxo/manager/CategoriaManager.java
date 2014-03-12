package br.com.infox.epp.fluxo.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.fluxo.dao.CategoriaDAO;
import br.com.infox.epp.fluxo.entity.Categoria;

@Name(CategoriaManager.NAME)
@AutoCreate
public class CategoriaManager extends Manager<CategoriaDAO, Categoria> {

    private static final long serialVersionUID = 2649821908249070536L;

    public static final String NAME = "categoriaManager";

    @In
    private CategoriaDAO categoriaDAO;

    public List<Object[]> listProcessoByCategoria() {
        return categoriaDAO.listProcessoByCategoria();
    }
}
