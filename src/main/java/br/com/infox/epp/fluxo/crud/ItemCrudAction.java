package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractRecursiveCrudAction;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.manager.ItemManager;
import br.com.infox.epp.fluxo.tree.ItemTreeHandler;

@Name(ItemCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ItemCrudAction extends AbstractRecursiveCrudAction<Item, ItemManager> implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "itemCrudAction";

    @Override
    public String save() {
        getInstance().setAtivo(paiPermiteAtivo() && isAtivo());
        inativaFilhosSeInativo();
        return super.save();
    }

    private boolean isAtivo() {
        final Boolean ativo = getInstance().getAtivo();
        return ativo == null || ativo;
    }

    private boolean paiPermiteAtivo() {
        final Item itemPai = getInstance().getItemPai();
        return itemPai == null || itemPai.getAtivo();
    }

    private void inativaFilhosSeInativo() {
        final Item instance = getInstance();
        if (!instance.getAtivo()) {
            inactiveRecursive(instance);
        }
    }

    @Override
    public void newInstance() {
        super.newInstance();
        limparTrees();
    }

    protected void limparTrees() {
        final ItemTreeHandler ith = BeanManager.INSTANCE.getReference(ItemTreeHandler.class);
        if (ith != null) {
            ith.clearTree();
        }
    }
}
