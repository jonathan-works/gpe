package br.com.infox.epp.fluxo.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.fluxo.dao.ItemDAO;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.query.ItemQuery;

@Name(ItemManager.NAME)
@AutoCreate
@Stateless
public class ItemManager extends Manager<ItemDAO, Item> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "itemManager";

    public Set<Item> getFolhas(final Integer idPai) {
        final Item pai = find(idPai);
        return getFolhas(pai);
    }

    public Set<Item> getFolhas(final Item pai) {
        Set<Item> result = null;
        if (pai != null) {
            final HashMap<String, Object> params = new HashMap<>();
            params.put(ItemQuery.CAMINHO_COMPLETO, pai.getCaminhoCompleto());
            final List<Item> itemList = this.getDao().getResultList(ItemQuery.GET_FOLHAS_QUERY, params);
            result = new HashSet<>(itemList);
        } else {
            result = new HashSet<>();
        }
        return result;
    }

}
