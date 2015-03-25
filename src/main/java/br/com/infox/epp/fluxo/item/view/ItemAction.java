package br.com.infox.epp.fluxo.item.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.item.api.ItemManager;
import br.com.infox.epp.fluxo.item.api.ItemRepository;
import br.com.infox.kernel.manager.CrudManager;
import br.com.infox.kernel.repository.Repository;
import br.com.infox.kernel.view.AbstractAction;

@ManagedBean
@ViewScoped
public class ItemAction extends AbstractAction<Item, Integer> {
	
	@Inject
	private ItemManager itemManager;
	@Inject
	private ItemRepository itemRepository;
	
	@Override
	public void newInstance() {
		setInstance(new Item());
	}

	@Override
	protected CrudManager<Item> getCrudManager() {
		return itemManager;
	}

	@Override
	protected Repository<Item, Integer> getRepository() {
		return itemRepository;
	}
}
