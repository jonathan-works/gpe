package br.com.infox.epp.documento.component.suggest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.suggest.AbstractSuggestBean;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;

@Name(TipoModeloDocumentoSuggestBean.NAME)
@Scope(ScopeType.CONVERSATION)
public class TipoModeloDocumentoSuggestBean extends AbstractSuggestBean<TipoModeloDocumento> {

    public static final String NAME = "tipoModeloDocumentoSuggest";
    private static final long serialVersionUID = 1L;

    @Override
    public String getEjbql(String typed) {
        StringBuilder sb = new StringBuilder();
        sb.append("select new br.com.infox.componentes.suggest.SuggestItem(o.idTipoModeloDocumento, o.tipoModeloDocumento) "
                + "from TipoModeloDocumento o where lower(o.tipoModeloDocumento) ");
        sb.append("like lower(concat ('%', :");
        sb.append(INPUT_PARAMETER);
        sb.append(", '%')) ");
        sb.append("order by 1");
        return sb.toString();
    }

    @Override
    public TipoModeloDocumento load(Object id) {
        return entityManager.find(TipoModeloDocumento.class, id);
    }
}
