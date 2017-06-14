package br.com.infox.epp.fluxo.crud;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.dao.CategoriaDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;

@Named
@ViewScoped
public class NaturezaCategoriaFluxoCrudAction extends AbstractCrudAction<NaturezaCategoriaFluxo, NaturezaCategoriaFluxoManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private NaturezaCategoriaFluxoManager naturezaCategoriaFluxoManager;
    @Inject
    private CategoriaDAO categoriaDAO; 
    
    @Override
    protected void afterSave(String ret) {
        super.afterSave(ret);
        newInstance();
    }
    
    public List<Categoria> getCategorias() {
        return categoriaDAO.findAll();
    }

    @Override
    protected NaturezaCategoriaFluxoManager getManager() {
        return naturezaCategoriaFluxoManager;
    }
}
