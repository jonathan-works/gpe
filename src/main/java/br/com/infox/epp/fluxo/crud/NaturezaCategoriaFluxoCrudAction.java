package br.com.infox.epp.fluxo.crud;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;

@Name(NaturezaCategoriaFluxoCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class NaturezaCategoriaFluxoCrudAction extends AbstractCrudAction<NaturezaCategoriaFluxo> {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final String NAME = "naturezaCategoriaFluxoCrudAction";
    
    private List<Natureza> naturezaList;
    private List<Categoria> categoriaList;
    private List<Fluxo> fluxoList;

    @Create
    public void init() {
        naturezaList = getGenericManager().findAll(Natureza.class);
        categoriaList = getGenericManager().findAll(Categoria.class);
        fluxoList = getGenericManager().findAll(Fluxo.class);
        
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
