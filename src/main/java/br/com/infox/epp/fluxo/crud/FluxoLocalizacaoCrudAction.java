package br.com.infox.epp.fluxo.crud;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.tree.TreeHandler;
import br.com.infox.epp.access.component.tree.LocalizacaoFullTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.NatCatFluxoLocalizacaoManager;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;
import br.com.infox.seam.util.ComponentUtil;

@Name(FluxoLocalizacaoCrudAction.NAME)
public class FluxoLocalizacaoCrudAction extends AbstractCrudAction<NatCatFluxoLocalizacao, NatCatFluxoLocalizacaoManager> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final String NAME = "fluxoLocalizacaoCrudAction";

    @In
    private NaturezaCategoriaFluxoManager naturezaCategoriaFluxoManager;

    public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
        return getInstance().getNaturezaCategoriaFluxo();
    }

    public void setNaturezaCategoriaFluxo(
            NaturezaCategoriaFluxo naturezaCategoriaFluxo) {
        getInstance().setNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
    }

    public List<NaturezaCategoriaFluxo> getActiveNaturezaCategoriaFluxoListByFluxo(
            Fluxo fluxo) {
        return naturezaCategoriaFluxoManager.getActiveNaturezaCategoriaFluxoListByFluxo(fluxo);
    }

    @Override
    protected void afterSave(String ret) {
        newInstance();
        clearTree();
    }

    private void clearTree() {
        TreeHandler<?> treeHandler = ComponentUtil.getComponent(LocalizacaoFullTreeHandler.NAME);
        treeHandler.clearTree();
    }

    public void setLocalizacao(Localizacao localizacao) {
        if (localizacao == null || localizacao.getEstruturaPai() != null) {
            getInstance().setLocalizacao(localizacao);
        }
    }
    
    public Localizacao getLocalizacao() {
        return getInstance().getLocalizacao();
    }
}
