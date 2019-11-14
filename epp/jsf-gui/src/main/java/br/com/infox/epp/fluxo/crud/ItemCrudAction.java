package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.crud.AbstractRecursiveCrudAction;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.manager.ItemManager;
import br.com.infox.epp.fluxo.tree.ItemTreeHandler;

@Named
@ViewScoped
public class ItemCrudAction extends AbstractRecursiveCrudAction<Item, ItemManager> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ItemManager itemManager;

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
        final ItemTreeHandler ith = Beans.getReference(ItemTreeHandler.class);
        if (ith != null) {
            ith.clearTree();
        }
    }

    @Override
    protected ItemManager getManager() {
        return itemManager;
    }
}
