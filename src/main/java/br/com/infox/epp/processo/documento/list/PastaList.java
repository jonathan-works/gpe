package br.com.infox.epp.processo.documento.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.processo.documento.entity.Pasta;

@Name(PastaList.NAME)
@Scope(ScopeType.CONVERSATION)
public class PastaList extends EntityList<Pasta> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "pastaList";
    private final String DEFAULT_EJBQL = "select o from Pasta o";
    private final String DEFAULT_ORDER = "nome"; 
    
    @Override
    protected void addSearchFields() {
        addSearchField("processo", SearchCriteria.IGUAL);
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
