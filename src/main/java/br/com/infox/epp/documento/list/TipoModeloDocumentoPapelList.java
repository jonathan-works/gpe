package br.com.infox.epp.documento.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.TipoModeloDocumentoPapel;

@Named
@ViewScoped
public class TipoModeloDocumentoPapelList extends EntityList<TipoModeloDocumentoPapel> {

    private static final long serialVersionUID = 1L;

    public static final String DEFAULT_EJBQL = "select o from TipoModeloDocumentoPapel o";
    public static final String DEFAULT_ORDER = "o.papel.nome";

    @Override
    protected void addSearchFields() {
        addSearchField("tipoModeloDocumento", SearchCriteria.IGUAL);
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
        return null;
    }

}
