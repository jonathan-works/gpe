package br.com.infox.epp.meiocontato.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.meiocontato.entity.MeioContato;

@Name(MeioContatoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class MeioContatoList extends EntityList<MeioContato> {
	
    private static final long serialVersionUID = 1L;
    public static final String NAME = "meioContatoList";
    
    private static final String DEFAULT_EJBQL = "select o from MeioContato o";
    private static final String DEFAULT_ORDER = "idMeioContato";
    
    @Override
    protected void addSearchFields() {
        addSearchField("pessoa", SearchCriteria.IGUAL);
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