package br.com.infox.epp.documento.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumentoPapel;

@Named
@ViewScoped
public class ClassificacaoDocumentoPapelList extends EntityList<ClassificacaoDocumentoPapel> {

    private static final long serialVersionUID = 1L;

    public static final String DEFAULT_EJBQL = "select o from ClassificacaoDocumentoPapel o";
    public static final String DEFAULT_ORDER = "papel";

    @Override
    protected void addSearchFields() {
        addSearchField("classificacaoDocumento", SearchCriteria.IGUAL);
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
