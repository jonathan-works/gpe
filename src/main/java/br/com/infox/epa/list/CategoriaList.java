package br.com.infox.epa.list;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epa.entity.Categoria;
import br.com.itx.util.ComponentUtil;

@Name(CategoriaList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class CategoriaList extends EntityList<Categoria> {
	
	public static final String NAME = "categoriaList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from Categoria o";
	private static final String DEFAULT_ORDER = "categoria";
	 
	@Override
	protected void addSearchFields() {
		addSearchField("categoria", SearchCriteria.contendo);
		addSearchField("ativo", SearchCriteria.igual);
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("fluxo", "fluxo.fluxo");
		return map;
	}

	@Override
	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}
	
	public static CategoriaList instance(){
		return ComponentUtil.getComponent(CategoriaList.NAME);
	}
	
}