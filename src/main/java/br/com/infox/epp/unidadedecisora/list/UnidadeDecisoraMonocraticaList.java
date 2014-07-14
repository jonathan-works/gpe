package br.com.infox.epp.unidadedecisora.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

@Name(UnidadeDecisoraMonocraticaList.NAME)
@Scope(ScopeType.PAGE)
public class UnidadeDecisoraMonocraticaList extends EntityList<UnidadeDecisoraMonocratica> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraMonocraticaList";
	
	private static final String DEFAULT_EJBQL = "select o from UnidadeDecisoraMonocratica o";
    private static final String DEFAULT_ORDER = "nome";
    
	@Override
	protected void addSearchFields() {
		addSearchField("nome", SearchCriteria.CONTENDO);
		addSearchField("localizacao", SearchCriteria.IGUAL);
		addSearchField("ativo", SearchCriteria.IGUAL);
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
