package br.com.infox.epp.fluxo.list;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Item;

@Named
@ViewScoped
public class ItemList extends EntityList<Item> {

    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE = "/Item/itemTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Item.xls";

    private static final String DEFAULT_EJBQL = "select o from Item o";
    private static final String DEFAULT_ORDER = "caminhoCompleto";

    /**
     * Restricao por seleção de um item (o.itemPai)
     */
    private static final String R1 = "o.caminhoCompleto like concat("
            + "#{itemList.entity.itemPai.caminhoCompleto}, '%')";

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
    public String getTemplate() {
        return TEMPLATE;
    }

    @Override
    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }

}
