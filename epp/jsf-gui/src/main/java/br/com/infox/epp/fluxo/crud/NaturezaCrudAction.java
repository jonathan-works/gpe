package br.com.infox.epp.fluxo.crud;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.list.NaturezaCategoriaFluxoList;
import br.com.infox.epp.fluxo.manager.NaturezaManager;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;

@Named
@ViewScoped
public class NaturezaCrudAction extends AbstractCrudAction<Natureza, NaturezaManager> {

    private static final long serialVersionUID = 1L;

    @Inject
    private NaturezaManager naturezaManager;
    @Inject
    private NaturezaCategoriaFluxoCrudAction naturezaCategoriaFluxoCrudAction;
    @Inject
    private NaturezaCategoriaFluxoList naturezaCategoriaFluxoList;

    @Override
    public void newInstance() {
        super.newInstance();
        getInstance().setHasPartes(false);
    }

    @Override
    protected boolean isInstanceValid() {
        final Natureza natureza = getInstance();
        final Boolean hasPartes = natureza.getHasPartes();
        if (hasPartes == null) {
            return false;
        }
        if (hasPartes) {
            if (natureza.getTipoPartes() == null) {
                return false;
            }
            switch (natureza.getTipoPartes()) {
            case F:
                return natureza.getNumeroPartesFisicas() != null;
            case J:
                return natureza.getNumeroPartesJuridicas() != null;
            case A:
                return natureza.getNumeroPartesFisicas() != null || natureza.getNumeroPartesJuridicas() != null;
            default:
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    protected void beforeSave() {
        final Natureza natureza = getInstance();
        if (!natureza.getHasPartes()) {
            natureza.setTipoPartes(null);
            natureza.setNumeroPartesFisicas(null);
            natureza.setNumeroPartesJuridicas(null);
        } else if (natureza.apenasPessoaFisica()) {
            natureza.setNumeroPartesJuridicas(null);
        } else if (natureza.apenasPessoaJuridica()) {
            natureza.setNumeroPartesFisicas(null);
        }
    }

    public void onClickNaturezaCategoriaFluxoTab() {
        naturezaCategoriaFluxoCrudAction.getInstance().setNatureza(getInstance());
        naturezaCategoriaFluxoList.getEntity().setNatureza(getInstance());
    }

    public ParteProcessoEnum[] getTiposDePartes() {
        return ParteProcessoEnum.values();
    }

    @Override
    protected NaturezaManager getManager() {
        return naturezaManager;
    }
}
