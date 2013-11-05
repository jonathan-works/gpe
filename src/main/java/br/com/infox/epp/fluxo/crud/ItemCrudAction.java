package br.com.infox.epp.fluxo.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.entity.Item;

@Name(ItemCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ItemCrudAction extends AbstractCrudAction<Item> {

    public static final String NAME = "itemCrudAction";
    
    public String inactive(Item item) {
        return inactiveRecursive(item);
    }
    
    @Override
    public String save() {
        if (!getInstance().getAtivo()){
            inactiveRecursive(getInstance());
        }
        return super.save();
    }
    
}
