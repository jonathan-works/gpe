package br.com.infox.epa.action.crud;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.epa.entity.Categoria;
import br.com.infox.epa.entity.CategoriaItem;
import br.com.infox.epa.manager.CategoriaItemManager;
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

	@In
	private CategoriaItemManager categoriaItemManager;
	
	private List<CategoriaItem> categoriaItemList;
	private List<Item> itemList;
	private Categoria categoria;
	
	@Override
	protected boolean beforePersistOrUpdate() {
		getInstance().setCategoria(categoria);
		List<CategoriaItem> list = getEntityManager().createQuery("select o from CategoriaItem o where o.categoria=:categoria and o.item=:item").setParameter("item", getInstance().getItem()).setParameter("categoria", getInstance().getCategoria()).getResultList();
		if (!list.isEmpty())	{
			return false;
		}
		
		return super.beforePersistOrUpdate();
	}
	
	@Override
	protected String afterPersistOrUpdate(String ret) {
		newInstance();
		listByCategoria();
		return super.afterPersistOrUpdate(ret);
	}
	
	@Override
	public String remove(CategoriaItem obj) {
		String remove = super.remove(obj);
		if(remove != null) {
			categoriaItemList.remove(obj);
		}
		newInstance();
		return remove;
	}

	public void removeAll() {
		try {
			for (Iterator<CategoriaItem>  iterator = categoriaItemList.iterator(); iterator.hasNext();) {
				CategoriaItem ca = iterator.next();
					getEntityManager().remove(ca);
				iterator.remove();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		FacesMessages.instance().add("Registros removidos com sucesso!");
	}
		
	public void init() {
		CategoriaAction naturezaAction = (CategoriaAction) Component.getInstance(CategoriaAction.NAME);
		categoria = naturezaAction.getInstance();
		listByCategoria();
		itemList = categoriaItemManager.findAll(Item.class);
		newInstance();
	}

	private void listByCategoria() {
		categoriaItemList = categoriaItemManager.listByCategoria(categoria);
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

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public List<Item> getFolhas(int idPai)	{
		Item pai = getEntityManager().find(Item.class, idPai);
		List<Item> list = pai.getItemList();
		
		List<Item> result = new ArrayList<Item>();
		if (list.size() == 0)	{
			result.add(pai);
			return result;
		}
		
		for (Item filho : list) {
			result.addAll(getFolhas(filho.getIdItem()));
		}
		
		return result;
	}
	
	@Override
	public String persist() {
		List<Item> folhas = getFolhas(getInstance().getItem().getIdItem());
		
		for (Item item : folhas) {
			if (item.getAtivo())	{
				getInstance().setItem(item);
				super.persist();
			}
		}
		return "persisted";
	}
	
}