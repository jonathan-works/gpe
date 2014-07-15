package br.com.infox.epp.documento.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.documento.entity.ExtensaoArquivo;

@Name(ExtensaoArquivoList.NAME)
@Scope(ScopeType.PAGE)
public class ExtensaoArquivoList extends EntityList<ExtensaoArquivo> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "extensaoArquivoList";
    
    private static final String DEFAULT_EJBQL = "select o from ExtensaoArquivo o";
    private static final String DEFAULT_ORDER = "nomeExtensao";
    
    @Override
    protected void addSearchFields() {
        addSearchField("nomeExtensao", SearchCriteria.CONTENDO);
        addSearchField("extensao", SearchCriteria.CONTENDO);
        addSearchField("tipoProcessoDocumento", SearchCriteria.IGUAL);
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
