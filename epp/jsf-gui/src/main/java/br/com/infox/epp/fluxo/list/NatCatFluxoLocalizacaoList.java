package br.com.infox.epp.fluxo.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NatCatFluxoLocalizacao;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;

@Named
@ViewScoped
public class NatCatFluxoLocalizacaoList extends EntityList<NatCatFluxoLocalizacao> {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from NatCatFluxoLocalizacao o";
    private static final String DEFAULT_ORDER = "naturezaCategoriaFluxo.natureza";

    public void setFluxo(Fluxo fluxo) {
        newInstance();
        getEntity().setNaturezaCategoriaFluxo(new NaturezaCategoriaFluxo());
        getEntity().getNaturezaCategoriaFluxo().setFluxo(fluxo);
    }

    @Override
    protected void addSearchFields() {
        addSearchField("naturezaCategoriaFluxo.natureza", SearchCriteria.IGUAL);
        addSearchField("naturezaCategoriaFluxo.categoria", SearchCriteria.IGUAL);
        addSearchField("naturezaCategoriaFluxo.fluxo", SearchCriteria.IGUAL);
        addSearchField("localizacao", SearchCriteria.IGUAL);
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_EJBQL;
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

}
