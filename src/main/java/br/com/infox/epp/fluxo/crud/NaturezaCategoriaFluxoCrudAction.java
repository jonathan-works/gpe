package br.com.infox.epp.fluxo.crud;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.dao.CategoriaDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;

@Name(NaturezaCategoriaFluxoCrudAction.NAME)
public class NaturezaCategoriaFluxoCrudAction extends AbstractCrudAction<NaturezaCategoriaFluxo, NaturezaCategoriaFluxoManager> {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "naturezaCategoriaFluxoCrudAction";
    
    @In
    private CategoriaDAO categoriaDAO; 
    
    @Override
    protected void afterSave(String ret) {
        super.afterSave(ret);
        newInstance();
    }
    
    public List<Categoria> getCategorias() {
        return categoriaDAO.findAll();
    }

}
