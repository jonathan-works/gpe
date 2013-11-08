package br.com.infox.epp.fluxo.crud;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.NatCatFluxoLocalizacaoManager;
import br.com.infox.ibpm.component.tree.LocalizacaoTreeHandler;
import br.com.itx.util.ComponentUtil;

@Name(NatCatFluxoLocalizacaoCrudAction.NAME)
public class NatCatFluxoLocalizacaoCrudAction extends AbstractCrudAction<NatCatFluxoLocalizacao> {
    
    public static final String NAME = "natCatFluxoLocalizacaoCrudAction";
    
    private NaturezaCategoriaFluxo naturezaCategoriaFluxoCorrente;
    @In private NatCatFluxoLocalizacaoManager natCatFluxoLocalizacaoManager;
    
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
    public String save() {
        if (getInstance().getHeranca()) {
            natCatFluxoLocalizacaoManager.persistWithChildren(getInstance());
        }
        return super.save();
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
