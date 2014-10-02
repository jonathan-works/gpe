package br.com.infox.epp.fluxo.list;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;

@Name(NaturezaCategoriaProcessoList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class NaturezaCategoriaProcessoList extends EntityList<NaturezaCategoriaFluxo> {
    public static final String NAME = "natCatProcessoList";
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select ncf from NatCatFluxoLocalizacao o "
            + "inner join o.naturezaCategoriaFluxo ncf "
            + "inner join ncf.fluxo.fluxoPapelList papelList where ncf.fluxo.publicado is true "
            + "and (current_date() >= ncf.fluxo.dataInicioPublicacao and "
            + "(ncf.fluxo.dataFimPublicacao is null or current_date() < ncf.fluxo.dataFimPublicacao)) "
            + "and papelList.papel = #{usuarioLogadoPerfilAtual.getPerfilTemplate().getPapel()} ";
    private static final String DEFAULT_ORDER = "natureza";
    
    @Override
    protected void addSearchFields() {
    }

    @Override
    protected String getDefaultEjbql() {
        Localizacao localizacao = Authenticator.getUsuarioPerfilAtual().getPerfilTemplate().getLocalizacao();
        if (localizacao == null) {
            return DEFAULT_EJBQL + " and o.localizacao is null";
        }
        return DEFAULT_EJBQL + " and o.localizacao.idLocalizacao = " + localizacao.getIdLocalizacao();
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
