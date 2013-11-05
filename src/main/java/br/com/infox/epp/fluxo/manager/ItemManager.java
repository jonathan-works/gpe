package br.com.infox.epp.fluxo.manager;

import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.fluxo.entity.Item;

@Name(ItemManager.NAME)
@AutoCreate
public class ItemManager extends GenericManager {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "itemManager";
    
    public Set<Item> getFolhas(Integer idPai) {
        Item pai = find(Item.class, idPai);
        return getFolhas(pai);
    }
    
    public Set<Item> getFolhas(Item pai) {
        Set<Item> result = null;
        if (pai != null) {
            Set<Item> set = new HashSet<Item>(pai.getItemList());
            result = new HashSet<Item>();
            if (set.size() == 0) {
                result.add(pai);
            } else {
                for (Item filho : set) {
                    result.addAll(getFolhas(filho));
                }
            }
        }
        return result;
    }

}
