package br.com.infox.epp.fluxo.crud;

import java.util.Set;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.CategoriaItem;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.manager.CategoriaItemManager;
import br.com.infox.epp.fluxo.manager.ItemManager;
import br.com.infox.epp.fluxo.tree.ItemTreeHandler;

@Name(CategoriaItemCrudAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class CategoriaItemCrudAction extends AbstractCrudAction<CategoriaItem, CategoriaItemManager> {
    
	private static final long serialVersionUID = 1L;

    public static final String NAME = "categoriaItemCrudAction";
    public static final String REG_INATIVO_MSG = "Registro inativo não pôde ser inserido.";
    
    @In
    private ItemManager itemManager;

    private Item item;
    private Categoria categoria;

    public Item getItem() {
        return item;
    }

    public void setItem(final Item item) {
        this.item = item;
    }
    
    @Override
    public String save() {
    	if (!item.getAtivo()){
    		getMessagesHandler().add(REG_INATIVO_MSG);
    		return null;
    	}
        final Set<Item> folhas = itemManager.getFolhas(item);
        boolean inserted = false;
        for (final Item folha : folhas) {
            if (!folha.getAtivo()) {
                continue;
            }
            final CategoriaItem instance = getInstance();
            instance.setItem(folha);
            instance.setCategoria(categoria);
            inserted = PERSISTED.equals(super.save()) || inserted;
        }

        limparTreeDeItem();
        return inserted ? PERSISTED : null;
    }

    @Override
    public void newInstance() {
        super.newInstance();
        this.item = null;
    }

    @Override
    protected void afterSave(final String ret) {
        if (PERSISTED.equals(ret)) {
            newInstance();
        }
    }

    private void limparTreeDeItem() {
        final ItemTreeHandler ite = (ItemTreeHandler) Component.getInstance(ItemTreeHandler.NAME);
        if (ite != null) {
            ite.clearTree();
        }
    }

    @Override
    public String remove(final CategoriaItem categoriaItem) {
        categoria.getCategoriaItemList().remove(categoriaItem);
        return super.remove(categoriaItem);
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(final Categoria categoria) {
        this.categoria = categoria;
    }
}
