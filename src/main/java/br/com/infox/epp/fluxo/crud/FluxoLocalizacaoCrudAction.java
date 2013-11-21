package br.com.infox.epp.fluxo.crud;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;
import br.com.itx.util.ComponentUtil;

@Name(FluxoLocalizacaoCrudAction.NAME)
public class FluxoLocalizacaoCrudAction extends AbstractCrudAction<NatCatFluxoLocalizacao> {

    public static final String NAME = "fluxoLocalizacaoCrudAction";
    
    @In NaturezaCategoriaFluxoManager naturezaCategoriaFluxoManager;
    
    public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo(){
        return getInstance().getNaturezaCategoriaFluxo();
    }
    
    public void setNaturezaCategoriaFluxo(NaturezaCategoriaFluxo naturezaCategoriaFluxo){
        getInstance().setNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
    }
    
    public List<NaturezaCategoriaFluxo> getActiveNaturezaCategoriaFluxoListByFluxo(Fluxo fluxo) {
        return naturezaCategoriaFluxoManager.getActiveNaturezaCategoriaFluxoListByFluxo(fluxo);
    }
    
    @Override
    protected void afterSave() {
        super.afterSave();
        newInstance();
        clearTree();
    }

    private void clearTree() {
        LocalizacaoTreeHandler treeHandler = ComponentUtil.getComponent("localizacaoTree");
        treeHandler.clearTree();
    }
    
}
