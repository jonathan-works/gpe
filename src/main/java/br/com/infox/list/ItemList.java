package br.com.infox.list;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.itx.util.ComponentUtil;

@Name(ItemList.NAME)
@Scope(ScopeType.PAGE)
public class ItemList extends EntityList<Item> {
	
	public static final String NAME = "itemList";

	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE = "/Item/itemTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Items.xls";
	
	private static final String DEFAULT_EJBQL = "select o from Item o";
	private static final String DEFAULT_ORDER = "caminhoCompleto";
	
	/**
	 * Restricao por seleção de um item (o.itemPai)
	 */
	private static final String R1 = "o.caminhoCompleto like concat(" +
									"#{itemList.entity.itemPai.caminhoCompleto}, '%')";


	public static final ItemList instance() {
		return ComponentUtil.getComponent(ItemList.NAME);
	}
	
	protected void addSearchFields() {
		addSearchField("descricaoItem", SearchCriteria.CONTENDO);
		addSearchField("itemPai", SearchCriteria.CONTENDO, R1);
		addSearchField("codigoItem", SearchCriteria.CONTENDO);
		addSearchField("ativo", SearchCriteria.IGUAL);
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
	
	@Override
    public EntityList<Item> getBeanList() {
        return ItemList.instance();
    }
    
    @Override
    public String getTemplate() {
        return TEMPLATE;
    }
    
    @Override
    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }

}