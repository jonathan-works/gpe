package br.com.infox.epp.fluxo.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractRecursiveCrudAction;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.tree.ItemTreeHandler;
import br.com.itx.util.ComponentUtil;

@Name(ItemCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ItemCrudAction extends AbstractRecursiveCrudAction<Item> {

    public static final String NAME = "itemCrudAction";
    
    protected boolean beforeSave() {
        final Item item = getInstance();
        final Item itemPai = item.getItemPai();
        if (itemPai != null && !itemPai.getAtivo()){
            item.setAtivo(Boolean.FALSE);
        }
        return super.beforeSave();
    }
    
    @Override
    public String save() {
        final Item item = getInstance();

        String save = null;
        if (item.getAtivo() != null) {
            if (!item.getAtivo()){
                inactiveRecursive(item);
            }
            save = super.save();
        }
        return save;
    }

    @Override
    public void newInstance() {
    	super.newInstance();
    	limparTrees();
    }
    
    protected void limparTrees(){
        final ItemTreeHandler ith = ComponentUtil.getComponent(ItemTreeHandler.NAME);
        if (ith != null) {
            ith.clearTree();   
        }
    }
}
