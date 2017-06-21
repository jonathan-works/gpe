package br.com.infox.epp.fluxo.crud;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.access.component.tree.LocalizacaoFullTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.NatCatFluxoLocalizacaoManager;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;

@Named
@ViewScoped
public class FluxoLocalizacaoCrudAction extends AbstractCrudAction<NatCatFluxoLocalizacao, NatCatFluxoLocalizacaoManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private NaturezaCategoriaFluxoManager naturezaCategoriaFluxoManager;
    @Inject
    private NatCatFluxoLocalizacaoManager natCatFluxoLocalizacaoManager;

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
        Beans.getReference(LocalizacaoFullTreeHandler.class).clearTree();
    }

    public void setLocalizacao(Localizacao localizacao) {
        if (localizacao == null || localizacao.getEstruturaPai() != null) {
            getInstance().setLocalizacao(localizacao);
        }
    }
    
    public Localizacao getLocalizacao() {
        return getInstance().getLocalizacao();
    }

    @Override
    protected NatCatFluxoLocalizacaoManager getManager() {
        return natCatFluxoLocalizacaoManager;
    }
}
