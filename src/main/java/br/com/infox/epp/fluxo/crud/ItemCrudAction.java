package br.com.infox.epp.fluxo.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractRecursiveCrudAction;
import br.com.infox.epp.fluxo.entity.Item;

@Name(ItemCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ItemCrudAction extends AbstractRecursiveCrudAction<Item> {

    public static final String NAME = "itemCrudAction";
    
    public void inactive(Item item) {
        inactiveRecursive(item);
    }
    
    protected boolean beforeSave() {
        if (getInstance().getItemPai() != null && !getInstance().getItemPai().getAtivo()){
            getInstance().setAtivo(false);
        }
        return super.beforeSave();
    }
    
    @Override
    public String save() {
        if (!getInstance().getAtivo()){
            inactiveRecursive(getInstance());
        }
        return super.save();
    }
    
}
