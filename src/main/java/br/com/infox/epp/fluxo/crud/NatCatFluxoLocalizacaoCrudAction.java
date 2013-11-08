package br.com.infox.epp.fluxo.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;

@Name(NatCatFluxoLocalizacaoCrudAction.NAME)
public class NatCatFluxoLocalizacaoCrudAction extends AbstractCrudAction<NatCatFluxoLocalizacao> {
    
    public static final String NAME = "natCatFluxoLocalizacaoCrudAction";
    
    private NaturezaCategoriaFluxo naturezaCategoriaFluxoCorrente;
    
    public void setNaturezaCategoriaFluxo(NaturezaCategoriaFluxo naturezaCategoriaFluxo){
        getInstance().setNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
        naturezaCategoriaFluxoCorrente = naturezaCategoriaFluxo;
    }
    
    @Override
    public void newInstance() {
        super.newInstance();
        getInstance().setNaturezaCategoriaFluxo(naturezaCategoriaFluxoCorrente);
    }
    
    @Override
    protected void afterSave() {
        super.afterSave();
        newInstance();
    }
    
}
