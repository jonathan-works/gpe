package br.com.infox.epp.fluxo.crud;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.manager.NaturezaManager;
import br.com.infox.epp.processo.partes.type.ParteProcessoEnum;

@Name(NaturezaCrudAction.NAME)
public class NaturezaCrudAction extends AbstractCrudAction<Natureza, NaturezaManager> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "naturezaCrudAction";
    
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
            return natureza.getTipoPartes() != null
                    && natureza.getNumeroPartesFisicas() != null;
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

    public ParteProcessoEnum[] getTiposDePartes() {
        return ParteProcessoEnum.values();
    }

}
