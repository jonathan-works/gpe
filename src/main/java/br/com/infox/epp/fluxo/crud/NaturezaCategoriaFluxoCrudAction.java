package br.com.infox.epp.fluxo.crud;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;

@Name(NaturezaCategoriaFluxoCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class NaturezaCategoriaFluxoCrudAction extends AbstractCrudAction<NaturezaCategoriaFluxo, NaturezaCategoriaFluxoManager> {
    
    private static final long serialVersionUID = 1L;

    public static final String NAME = "naturezaCategoriaFluxoCrudAction";
    
    @In
    private GenericManager genericManager;
    
    private List<Natureza> naturezaList;
    private List<Categoria> categoriaList;
    private List<Fluxo> fluxoList;

    @Create
    public void init() {
        naturezaList = genericManager.findAll(Natureza.class);
        categoriaList = genericManager.findAll(Categoria.class);
        fluxoList = genericManager.findAll(Fluxo.class);
        
    }

    public List<Natureza> getNaturezaList() {
        return naturezaList;
    }

    public List<Categoria> getCategoriaList() {
        return categoriaList;
    }

    public List<Fluxo> getFluxoList() {
        return fluxoList;
    }

}
