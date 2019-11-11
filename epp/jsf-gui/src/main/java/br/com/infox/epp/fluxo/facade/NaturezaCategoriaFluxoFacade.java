package br.com.infox.epp.fluxo.facade;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.manager.CategoriaManager;
import br.com.infox.epp.fluxo.manager.NaturezaManager;

@Scope(ScopeType.CONVERSATION)
@Name(NaturezaCategoriaFluxoFacade.NAME)
public class NaturezaCategoriaFluxoFacade implements Serializable {

    public static final String NAME = "naturezaCategoriaFluxoFacade";
    private static final long serialVersionUID = 1L;

    private List<Natureza> naturezaList;
    private List<Categoria> categoriaList;
    private List<Fluxo> fluxoList;

    @In
    private NaturezaManager naturezaManager;
    @In
    private CategoriaManager categoriaManager;
    
    @In
    private GenericManager genericManager;

    @Create
    public void init() {
        naturezaList = naturezaManager.getNaturezasPrimarias();
        fluxoList = genericManager.findAll(Fluxo.class);
    }

    
    public void loadCategoriaList(Natureza natureza){
    	categoriaList = categoriaManager.getCategoriasByNatureza(natureza);
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
