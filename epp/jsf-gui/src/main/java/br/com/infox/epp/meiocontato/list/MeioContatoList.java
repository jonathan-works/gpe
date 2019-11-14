package br.com.infox.epp.meiocontato.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.meiocontato.entity.MeioContato;

@Named
@ViewScoped
public class MeioContatoList extends EntityList<MeioContato> {
	
    private static final long serialVersionUID = 1L;
    
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