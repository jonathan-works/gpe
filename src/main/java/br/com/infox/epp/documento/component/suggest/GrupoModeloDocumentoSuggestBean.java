package br.com.infox.epp.documento.component.suggest;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.suggest.AbstractSuggestBean;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;

@Name(GrupoModeloDocumentoSuggestBean.NAME)
@Install(precedence = Install.FRAMEWORK)
public class GrupoModeloDocumentoSuggestBean extends AbstractSuggestBean<GrupoModeloDocumento> {
    public static final String NAME = "grupoModeloDocumentoSuggest";

    private static final long serialVersionUID = 1L;

    @Override
    public String getEjbql() {
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
