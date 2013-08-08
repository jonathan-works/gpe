package br.com.infox.epp.action.crud;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.Messages;
import org.jboss.seam.international.StatusMessage;

import br.com.infox.epp.entity.Categoria;
import br.com.infox.epp.entity.CategoriaItem;
import br.com.infox.epp.manager.CategoriaItemManager;
import br.com.infox.ibpm.entity.Item;
import br.com.itx.component.AbstractHome;

/**
 * 
 * @author Daniel
 * 
 */
@Name(CategoriaItemAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class CategoriaItemAction extends AbstractHome<CategoriaItem> {

    private static final long serialVersionUID = 1L;

    public static final String NAME = "categoriaItemAction";
    private static final String PERSISTED = "persisted";

    @In
    private CategoriaItemManager categoriaItemManager;

    private List<CategoriaItem> categoriaItemList;
    private List<Item> itemList;

    @Override
    protected String afterPersistOrUpdate(String ret) {
        newInstance();
        listByCategoria();
        return ret;
    }

    @Override
    public String remove(CategoriaItem obj) {
        String remove = super.remove(obj);
        if ("removed".equals(remove)) {
            categoriaItemList.remove(obj);
        }
        newInstance();
        return remove;
    }

    @Override
    public void create() {
        super.create();
        listByCategoria();
        itemList = categoriaItemManager.findAll(Item.class);
        newInstance();
    }

    private void listByCategoria() {
        Categoria otherCategoria = categoriaItemManager.getCategoriaAtual();
        CategoriaItem instance = getInstance();
        Categoria categoria = instance.getCategoria();

        if (categoria == null || !categoria.equals(otherCategoria)) {
            instance.setCategoria(otherCategoria);
            categoriaItemList = categoriaItemManager.listByCategoria(instance
                    .getCategoria());
        }

    }

    public void setCategoriaItemList(List<CategoriaItem> categoriaItemList) {
        this.categoriaItemList = categoriaItemList;
    }

    public List<CategoriaItem> getCategoriaItemList() {
        return categoriaItemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public List<Item> getItemList() {
        return itemList;
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

    public Set<Item> getFolhas(Integer idPai) {
        Item pai = getEntityManager().find(Item.class, idPai);
        return getFolhas(pai);
    }

    @Override
    public String persist() {
        Set<Item> folhas = getFolhas(getInstance().getItem());
        boolean hasInserted = false;
        if (folhas != null) {
            Categoria categoria = categoriaItemManager.getCategoriaAtual();
            for (Item item : folhas) {
                if (item.getAtivo()) {
                    getInstance().setItem(item);
                    getInstance().setCategoria(categoria);

                    hasInserted = PERSISTED.equals(super.persist())
                            || hasInserted;
                }
            }
        }

        String result;
        if (hasInserted) {
            result = PERSISTED;
            FacesMessages.instance().clear();
            FacesMessages.instance().add(
                    Messages.instance().get("CategoriaItem_created"));
        } else {
            FacesMessages.instance().clear();
            FacesMessages.instance().add(StatusMessage.Severity.ERROR,
                    getEntityExistsExceptionMessage());
            result = "";
        }

        return result;
    }

}