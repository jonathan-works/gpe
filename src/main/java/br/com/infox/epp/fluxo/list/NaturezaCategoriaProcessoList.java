package br.com.infox.epp.fluxo.list;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;

@Name(NaturezaCategoriaProcessoList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class NaturezaCategoriaProcessoList extends EntityList<NaturezaCategoriaFluxo> {
    public static final String NAME = "natCatProcessoList";
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select ncf from NatCatFluxoLocalizacao o "
            + "inner join o.naturezaCategoriaFluxo ncf "
            + "inner join ncf.fluxo.fluxoPapelList papelList";
    private static final String DEFAULT_ORDER = "natureza";
    private static final String R1 = "o.localizacao = #{usuarioLogadoLocalizacaoAtual.getLocalizacao()}";
    private static final String R2 = "papelList.papel = #{usuarioLogadoLocalizacaoAtual.getPapel()}";

    @Override
    protected void addSearchFields() {
        addSearchField("localizacao", SearchCriteria.IGUAL, R1);
        addSearchField("papel", SearchCriteria.IGUAL, R2);
    }

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_EJBQL;
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        final HashMap<String, String> order = new HashMap<>();
        order.put("natureza", "ncf.natureza");
        order.put("categoria", "ncf.categoria");
        order.put("fluxo", "ncf.fluxo");
        return order;
    }

}
