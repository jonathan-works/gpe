package br.com.infox.epp.documento.component.suggest;

import javax.inject.Named;

import br.com.infox.core.suggest.AbstractSuggestBean;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;

@Named(GrupoModeloDocumentoSuggestBean.NAME)
@ViewScoped
public class GrupoModeloDocumentoSuggestBean extends AbstractSuggestBean<GrupoModeloDocumento> {
    protected static final String NAME = "grupoModeloDocumentoSuggest";

    private static final long serialVersionUID = 1L;

    @Override
    public String getEjbql(String typed) {
        StringBuilder sb = new StringBuilder();
        sb.append("select new br.com.infox.componentes.suggest.SuggestItem(o.idGrupoModeloDocumento, o.grupoModeloDocumento) ");
        sb.append("from GrupoModeloDocumento o ");
        sb.append("where lower(grupoModeloDocumento) ");
        sb.append("like lower(concat ('%', :");
        sb.append(INPUT_PARAMETER);
        sb.append(", '%')) ");
        sb.append("and o.ativo = true order by 1");
        return sb.toString();
    }

    @Override
    public GrupoModeloDocumento load(Object id) {
        return entityManager.find(GrupoModeloDocumento.class, id);
    }

}
