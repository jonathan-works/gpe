package br.com.infox.epp.documento.list.associated;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.VariavelTipoModelo;

@Named
@ViewScoped
public class AssociatedTipoModeloVariavelList extends EntityList<VariavelTipoModelo> {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from VariavelTipoModelo o";
    private static final String DEFAULT_ORDER = "tipoModeloDocumento";

    @Override
    protected void addSearchFields() {
        addSearchField("tipoModeloDocumento", SearchCriteria.IGUAL);
        addSearchField("tipoModeloDocumento.grupoModeloDocumento", SearchCriteria.IGUAL);
        addSearchField("variavel", SearchCriteria.IGUAL);
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
