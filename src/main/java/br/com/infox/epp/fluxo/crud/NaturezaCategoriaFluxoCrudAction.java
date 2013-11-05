package br.com.infox.epp.fluxo.crud;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.itx.util.EntityUtil;

@Name(NaturezaCategoriaFluxoCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class NaturezaCategoriaFluxoCrudAction extends AbstractCrudAction<NaturezaCategoriaFluxo> {
    
    public static final String NAME = "naturezaCategoriaFluxoCrudAction";
    
    private List<Natureza> naturezaList;
    private List<Categoria> categoriaList;
    private List<Fluxo> fluxoList;
    
    @PostConstruct
    public void init() {
        naturezaList = EntityUtil.getEntityList(Natureza.class);
        categoriaList = EntityUtil.getEntityList(Categoria.class);
        fluxoList = EntityUtil.getEntityList(Fluxo.class);
        
    }

    public List<Natureza> getNaturezaList() {
        return naturezaList;
    }

    public void setNaturezaList(List<Natureza> naturezaList) {
        this.naturezaList = naturezaList;
    }

    public List<Categoria> getCategoriaList() {
        return categoriaList;
    }

    public void setCategoriaList(List<Categoria> categoriaList) {
        this.categoriaList = categoriaList;
    }

    public List<Fluxo> getFluxoList() {
        return fluxoList;
    }

    public void setFluxoList(List<Fluxo> fluxoList) {
        this.fluxoList = fluxoList;
    }
    
}
