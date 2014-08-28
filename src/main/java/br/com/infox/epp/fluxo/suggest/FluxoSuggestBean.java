package br.com.infox.epp.fluxo.suggest;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.suggest.AbstractSuggestBean;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Name(FluxoSuggestBean.NAME)
public class FluxoSuggestBean extends AbstractSuggestBean<Fluxo> {

    public static final String NAME = "fluxoSuggest";
    private static final long serialVersionUID = 1L;

    @Override
    public String getEjbql(String typed) {
        StringBuilder sb = new StringBuilder();
        sb.append("select new br.com.infox.componentes.suggest.SuggestItem(o.idFluxo, o.fluxo) from Fluxo o ");
        sb.append("where lower(fluxo) like lower(concat ('%', :");
        sb.append(INPUT_PARAMETER);
        sb.append(", '%')) ");
        sb.append("order by 1");
        return sb.toString();
    }

    @Override
    public Fluxo load(Object id) {
        return entityManager.find(Fluxo.class, id);
    }
}
