package br.com.infox.list;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.Item;

@Name(ItemList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class ItemList extends EntityList<Item> {
	
	public static final String NAME = "itemList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from Item o";
	private static final String DEFAULT_ORDER = "caminhoCompleto";
	
	/**
	 * Restricao por sele��o de um item (o.itemPai)
	 */
	private static final String R1 = "o.caminhoCompleto like concat(" +
									"#{itemList.entity.itemPai.caminhoCompleto}, '%')";


	protected void addSearchFields() {
		addSearchField("descricaoItem", SearchCriteria.contendo);
		addSearchField("itemPai", SearchCriteria.contendo, R1);
		addSearchField("codigoItem", SearchCriteria.contendo);
		addSearchField("ativo", SearchCriteria.igual);
	}

	protected Map<String, String> getCustomColumnsOrder() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("itemPai", "itemPai.descricaoItem");
		return map;
	}

	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}
	


}